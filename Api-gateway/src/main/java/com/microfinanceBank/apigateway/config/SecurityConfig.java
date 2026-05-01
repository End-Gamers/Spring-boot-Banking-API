package com.microfinanceBank.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * WebFlux 기반 보안 설정 클래스.
 * CSRF 비활성화, CORS 허용, OAuth2 JWT 인증, Swagger/Actuator 경로 공개 접근 허용을 구성한다.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

	/**
	 * 보안 필터 체인을 구성한다.
	 * Actuator 및 Swagger 경로는 인증 없이 허용하고, 나머지 모든 요청은 JWT 인증을 요구한다.
	 * 세션은 Stateless로 관리된다.
	 *
	 * @param http ServerHttpSecurity 객체
	 * @return 구성된 SecurityWebFilterChain
	 */
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
				.csrf().disable()
				.cors()
				.and()
				.formLogin().disable()
//				.headers(headerSpec -> headerSpec.contentSecurityPolicy("script-src 'self'"))
				.authorizeExchange()
				.pathMatchers("/actuator/**","/customer/actuator/**","/issue/actuator/**","/transaction/actuator/**",
						"/employee/actuator/**","/loan/actuator/**")
				.permitAll()
				.and()
	            .authorizeExchange()
				.pathMatchers("/webjars/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**",
						"/swagger-ui.html","/customer/v3/api-docs/**","/transaction/v3/api-docs/**",
						"/employee/v3/api-docs/**","/issue/v3/api-docs/**","/loan/v3/api-docs/**")
				.permitAll()
				.and()
				.authorizeExchange()
				.anyExchange()
				.authenticated()
				.and()
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.oauth2ResourceServer()
				.jwt();

		return http.build();

	}

	/** 정적 리소스(CSS, JS, 이미지 등)는 보안 필터 적용에서 제외한다. */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
				.antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico");
	}

	/** Stateless 환경에서 세션 인증을 비활성화하는 전략을 반환한다. */
	@Bean
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new NullAuthenticatedSessionStrategy();
	}
}
