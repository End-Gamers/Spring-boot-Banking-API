package com.microfinanceBank.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger / OpenAPI 문서 설정 클래스 (프로덕션 환경 제외).
 * Gateway에 등록된 라우트를 기반으로 각 마이크로서비스별 API 그룹을 자동 생성한다.
 */
@RequiredArgsConstructor
@Configuration
@Profile("!prod")
@Slf4j
public class SwaggerOpenApiConfig {
    private final RouteDefinitionLocator locator;

    /**
     * Gateway 라우트 정의에서 "-service" 로 끝나는 라우트를 읽어
     * Swagger UI에 마이크로서비스별 API 그룹을 등록한다.
     *
     * @param swaggerUiConfigParameters Swagger UI 파라미터
     * @param locator                   라우트 정의 로케이터
     * @return GroupedOpenApi 목록
     */
    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        for (RouteDefinition definition : definitions) {
            log.debug("id: " + definition.getId()+ "  "+definition.getUri().toString());
        }
        definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-service")).forEach(routeDefinition -> {
            String name = routeDefinition.getId().replaceAll("-service", "");
            swaggerUiConfigParameters.addGroup(name);
            GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
        });
        return groups;
    }

}
