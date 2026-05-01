package com.microfinanceBank.apigateway.filter;

import com.microfinanceBank.apigateway.repo.RedisCacheRepository;
import com.microfinanceBank.apigateway.model.CachedRequest;
import com.microfinanceBank.apigateway.model.CachedResponse;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.microfinanceBank.apigateway.utils.ConstantUtil.FILTER_PATTERNS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * Redis 캐시 및 XSS 방지 글로벌 필터 (redisCache 프로파일 전용).
 * GET 요청 응답을 Redis에 30분간 캐싱하고, 응답 본문에서 XSS 위험 패턴을 제거한다.
 * ADMIN 권한이 필요한 URL에 대해 역할 기반 접근 제어도 수행한다.
 */
@Slf4j
@Component
@Profile("redisCache")
public class RedisCacheAndXssFilter implements GlobalFilter, Ordered {


    private final RedisCacheRepository cache;
    private final ApplicationContext context;

    private static final String SWAGGER_URL="/v3/api-docs";
    Map<String,String> allowedUrls;
    @org.springframework.beans.factory.annotation.Value("${keycloakIssuerUri}")
    private  String issuerUri;

    public RedisCacheAndXssFilter(RedisCacheRepository redisCacheRepository, ApplicationContext context) {
        this.cache = redisCacheRepository;
        this.context = context;

        if (allowedUrls==null)
            this.allowedUrls=new ConcurrentHashMap<>(3);

        allowedUrls.put("/customer/api/customers","ADMIN");
        allowedUrls.put("/customer/api/accounts", "ADMIN");
        allowedUrls.put("/customer/api/account?accountNumber","ADMIN");
    }

    /**
     * 요청을 가로채어 Redis 캐시 조회, XSS 필터링, 권한 검사를 수행한다.
     * ADMIN 전용 GET URL에 캐시된 응답이 있으면 Redis에서 즉시 반환하고,
     * 없으면 다운스트림으로 요청을 전달한 뒤 응답을 캐시에 저장한다.
     */
    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var path = exchange.getRequest().getPath().value();
        boolean isGetMethod = isGetMethod(exchange);
        final var cachedRequest = getCachedRequest(exchange.getRequest());

        if (isGetMethod && allowedUrls.containsKey(path)){
             if (isGetMethod && allowedUrls.containsKey(path)) {
                 return cache.get(cachedRequest)
                         .map(Optional::of).defaultIfEmpty(Optional.empty())
                         .flatMap(response -> response.isPresent()?
                                 returnCachedResponse(exchange,path,response.get()) : completeNormalRequestFlow(exchange,chain,cachedRequest,path))
                         .then();

             }

         }
        return  completeNormalRequestFlow(exchange,chain,cachedRequest,path);
    }

    /** 응답을 가로채어 XSS 필터링 후 Redis에 저장하는 데코레이터를 반환한다. */
    private ServerHttpResponseDecorator getServerHttpResponse(ServerWebExchange exchange,CachedRequest cachedRequest,String path) {
        final var originalResponse = exchange.getResponse();
        final var dataBufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @NonNull
            @Override
            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {

                if (body instanceof Flux) {
                    final var flux = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(flux.buffer()
                            .map(dataBuffers -> {
                                final var outputStream = new ByteArrayOutputStream();
                                dataBuffers.forEach(dataBuffer -> {

                                    var responseContent = new byte[dataBuffer.readableByteCount()];

                                    dataBuffer.read(responseContent);

                                    try {
                                        outputStream.write(responseContent);
                                    } catch (IOException e) {
                                    }

                                });

                                //xss filter
                                try {
                                    var unfiltered = outputStream.toByteArray();
                                    outputStream.reset();
                                    var filtered=xssReplaceDangerousContents(unfiltered);
                                    outputStream.writeBytes(filtered);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }


                                boolean isGetMethod = isGetMethod(exchange);
                                if (Objects.requireNonNull(getStatusCode()).is2xxSuccessful() &&isGetMethod &&allowedUrls.containsKey(path)) {
                                    final var cachedResponse = new CachedResponse(getStatusCode(), getHeaders().toSingleValueMap(), outputStream.toByteArray());
                                    log.debug("Request {} Cached response {}", path, cachedResponse.getBody().toString());
                                    var cacheKey=getCachedRequest(exchange.getRequest());
                                    var a=cache.put(cacheKey, cachedResponse).subscribe();

                                }

                                return dataBufferFactory.wrap(outputStream.toString().getBytes());
                            }));
                }

                return super.writeWith(body).log();
            }
        };
    }

    /** 현재 요청이 HTTP GET 메서드인지 확인한다. */
    private static boolean isGetMethod(ServerWebExchange exchange) {
        boolean isGetMethod= exchange.getRequest().getMethodValue().equals("GET");
        return isGetMethod;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private CachedRequest getCachedRequest(ServerHttpRequest request) {
        return CachedRequest.builder()
                .method(request.getMethod())
                .path(request.getPath())
                .queryParams(request.getQueryParams())
                .build();
    }


    /** 바이트 배열에서 XSS 위험 패턴(스크립트 태그, eval 등)을 비동기로 제거한다. */
    @Async
    private byte[] xssReplaceDangerousContents(byte[] bytes) throws IOException {
        var data=new String(bytes);
        if (data != null) {
            for (Pattern pattern : FILTER_PATTERNS)
                data = pattern.matcher(data).replaceAll(EMPTY);
        }
        return data.getBytes();
    }

    /** JWT 토큰을 디코딩하여 사용자의 Realm 역할 집합을 반환한다. */
    private Set<String> userAuthorities(ServerWebExchange exchange){
        var path=exchange.getRequest().getPath().value();
        if (isSwaggerOrActuator(path))
            return null;
        var token=exchange.getRequest().getHeaders().get("Authorization").get(0).split(" ")[1];
        var jwt=JwtDecoders.fromIssuerLocation(issuerUri).decode(token);
        var userAuthorities=convert(jwt);
        return userAuthorities;
    }
    private String getKeycloakId(ServerWebExchange exchange){
        var id=exchange.getRequest().getHeaders().get("X-Keycloak-Id");
        return isNull(id)?null:id.get(0);
    }




    private Set<String> convert(Jwt jwt) {
        var claims= jwt.getClaims();
        final Map<String, List<String>> realmAccess = (Map<String, List<String>>) claims.get("realm_access");
        return realmAccess.get("roles")
                .stream()
                .collect(Collectors.toSet());
    }
    private boolean isSwaggerOrActuator(String path){
        return path.startsWith(SWAGGER_URL) || path.endsWith(SWAGGER_URL) || path.contains("actuator");
    }

    /** Redis에서 가져온 캐시된 응답을 사용자에게 반환한다. 권한이 없으면 403 을 반환한다. */
    private Mono<Void> returnCachedResponse(ServerWebExchange exchange, String path, CachedResponse response){
        Set<String> userAuthorities =userAuthorities(exchange);
        final var cachedRequest = getCachedRequest(exchange.getRequest());
        final var allowedRole=allowedUrls.get(path);

        if (!userAuthorities.contains(allowedRole)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        log.info("Return cached response for request: {}", cachedRequest);
        final var cachedResponse = response;
        final var serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(cachedResponse.getHttpStatus());
        MultiValueMap headerMap = new LinkedMultiValueMap();
        headerMap.set("cached-response","true");
        cachedResponse.getHeaders().entrySet()
                .stream().forEach(data -> headerMap.set(data.getKey(), data.getValue()));
        serverHttpResponse.getHeaders().addAll(headerMap);
        final var buffer = exchange.getResponse().bufferFactory().wrap(cachedResponse.getBody());

        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    /** 일반 요청 흐름을 완료한다: XSS 필터링 데코레이터를 적용하여 다운스트림 서비스로 요청을 전달한다. */
    private Mono<Void> completeNormalRequestFlow(ServerWebExchange exchange, GatewayFilterChain chain, CachedRequest cachedRequest, String path){
        final var mutatedHttpResponse = getServerHttpResponse(exchange, cachedRequest, path);
        mutatedHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return chain.filter(exchange.mutate().response(mutatedHttpResponse).build());
    }
}