package com.microfinanceBank.Issues;

import com.mongodb.reactivestreams.client.MongoClient;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "ISSUES API", version = "${springdoc.version}", description = "Documentation ISSUES API v1.0")
		,servers = {@Server(url = "http://localhost:8765/"+"${spring.application.name}"+"/")})
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
/**
 * 불만사항(Issues) 마이크로서비스의 진입점.
 * MongoDB 기반 Reactive 스트림으로 고객 불만사항을 처리한다.
 */
public class IssuesApplication {

	/** 엔티티-DTO 변환을 위한 ModelMapper 빈을 등록한다. */
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	/** 애플리케이션을 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(IssuesApplication.class, args);
	}

}
