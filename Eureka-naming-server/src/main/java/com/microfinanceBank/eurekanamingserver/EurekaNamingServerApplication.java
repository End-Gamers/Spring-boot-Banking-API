package com.microfinanceBank.eurekanamingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 서비스 디스커버리 서버 애플리케이션.
 * 모든 마이크로서비스는 이 서버에 등록되어 서로를 이름으로 탐색할 수 있다.
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaNamingServerApplication {

	/** Eureka 서버 애플리케이션을 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(EurekaNamingServerApplication.class, args);
	}

}
