package com.microfinanceBank.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.util.Objects.isNull;

/**
 * Redis 기반 API 속도 제한 필터 (redisCache 프로파일 전용).
 * 사용자별(Keycloak ID + 서비스명) 초당 요청 횟수를 Redis에 추적하여
 * 설정된 한도(REQUEST_PER_SECOND)를 초과하면 429 Too Many Requests 를 반환한다.
 */
@Component
@Slf4j
@Profile("redisCache")
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {
    private final ReactiveRedisTemplate<String,String> redisTemplate;
    private static final int REQUEST_PER_SECOND= 1;
    private static final String SWAGGER_URL="/v3/api-docs";


    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
            super(Config.class);
            this.redisTemplate = redisTemplate;


        }

        @Override
        public GatewayFilter apply(Config config) {
            //Custom Pre Filter.
            ReactiveValueOperations<String,String> ops=redisTemplate.opsForValue();

            return new OrderedGatewayFilter( (exchange, chain) -> {
                var path = exchange.getRequest().getPath().value();
                String serviceName=path.split("/")[1];
                var keycloakId=getKeycloakId(exchange);
                String userId=new StringBuffer(keycloakId).append("-").append(serviceName).toString();
                var db=getApiHitCount(ops,userId).flatMap(value->{
                    if (value.intValue()>=REQUEST_PER_SECOND && !isSwaggerOrActuator(path)){
                        log.info("User with keycloak  id {} has exceeded configured per day api limit. ",keycloakId);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("message","User limit has been exceeded");
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                });

                //Custom Post Filter.Suppose we can call error response handler based on error code.
                return db
                        .then(Mono.fromRunnable(() -> {
                })).zipWith(incrementApiHitCount(ops,userId)).then();
            },-2);
        }
    /** 요청 헤더(X-Keycloak-Id)에서 Keycloak 사용자 ID를 추출한다. */
    private String getKeycloakId(ServerWebExchange exchange){
        var id=exchange.getRequest().getHeaders().get("X-Keycloak-Id");
        return isNull(id)?"null":id.get(0);
    }

    /**
     * This method is to return the current number
     * of api calls made by this user from cache
     * @param userId - keycloak id + service name
     * @return Mono Integer - number of calls made by this user
     */
    public Mono<Integer> getApiHitCount(ReactiveValueOperations<String,String> ops,String userId) {
            return ops.get(userId).map(digit->Integer.valueOf(digit)).switchIfEmpty(Mono.defer(()->Mono.just(0)));

    }
    /**
     * This method is to increment the number of api
     * calls made by this user in cache
     * @param userId - keycloak userId + service name
     * @return Mono boolean
     */
    public Mono<Boolean> incrementApiHitCount(ReactiveValueOperations<String,String> ops, String userId) {
        return ops.increment(userId).zipWith(redisTemplate.expire(userId,Duration.ofSeconds(1))).hasElement();

    }
    /** 경로가 Swagger 또는 Actuator 엔드포인트인지 확인한다. 해당 경로는 속도 제한에서 제외된다. */
    private boolean isSwaggerOrActuator(String path){
        return path.startsWith(SWAGGER_URL) || path.endsWith(SWAGGER_URL) || path.contains("actuator");
    }

    /** RateLimitFilter 설정 내부 클래스. 추가 설정 프로퍼티를 여기에 정의한다. */
    public static class Config {

        public Config() {
        }



    }

    }

