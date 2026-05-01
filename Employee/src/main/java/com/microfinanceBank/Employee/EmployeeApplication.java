package com.microfinanceBank.Employee;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "EMPLOYEE API", version = "${springdoc.version}", description = "Documentation USER API v1.0")
		,servers = {@Server(url = "http://localhost:8765/"+"${spring.application.name}"+"/")
},security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
/**
 * 직원(Employee) 마이크로서비스의 진입점.
 * 직원 등록·삭제·권한 관리 및 지점 관리 기능을 제공하며 Keycloak과 연동된다.
 */
public class EmployeeApplication {
	/** BCrypt 패스워드 인코더 빈을 등록한다. */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/** 엔티티-DTO 변환을 위한 ModelMapper 빈을 등록한다. */
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	/** 애플리케이션을 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(EmployeeApplication.class, args);
	}

}
