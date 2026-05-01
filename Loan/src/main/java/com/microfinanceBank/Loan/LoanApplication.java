package com.microfinanceBank.Loan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "LOAN API", version = "${springdoc.version}", description = "Documentation LOAN API v1.0")
		,servers = {@Server(url = "http://localhost:8765/"+"${spring.application.name}"+"/")},security = @SecurityRequirement(name = "Bearer Authentication")
		)
@EnableDiscoveryClient
@EnableAsync
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
@EnableScheduling
/**
 * 대출(Loan) 마이크로서비스의 진입점.
 * 은행 대출·P2P 대출 신청·승인·상환 처리를 담당하며 Eureka, RabbitMQ와 연동된다.
 */
public class LoanApplication {

	/** 엔티티-DTO 변환을 위한 ModelMapper 빈을 등록한다. */
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	/** 애플리케이션을 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(LoanApplication.class, args);
	}

}
