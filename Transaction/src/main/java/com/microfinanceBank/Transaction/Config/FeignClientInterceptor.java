package com.microfinanceBank.Transaction.Config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * OpenFeign 클라이언트 인터셉터.
 * SecurityContext에서 JWT 토큰을 추출하여 Feign 요청의 Authorization 헤더에 추가한다.
 */
//@Component
@Configuration
@Async
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    /**
     * Feign 요청에 Bearer JWT 토큰을 Authorization 헤더로 삽입한다.
     *
     * @param template Feign 요청 템플릿
     */
    @Override
    public void apply(RequestTemplate template) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticatedPrincipal) {
            var  details = (Jwt) authentication.getPrincipal();
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, details.getTokenValue()));
        }
    }

}