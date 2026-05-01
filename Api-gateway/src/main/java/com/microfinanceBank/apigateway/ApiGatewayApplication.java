package com.microfinanceBank.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * API 게이트웨이 애플리케이션의 진입점.
 * 모든 마이크로서비스 요청을 라우팅하며, 캐싱·보안·속도 제한 기능을 제공한다.
 */
@SpringBootApplication
//@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
@EnableCaching
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
public class ApiGatewayApplication {

	/** 애플리케이션을 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}


}
