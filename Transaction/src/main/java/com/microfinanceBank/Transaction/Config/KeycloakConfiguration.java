package com.microfinanceBank.Transaction.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * "keycloak" 접두사 프로퍼티를 바인딩하는 Keycloak 설정 클래스.
 * 인증 서버 URL, 렐름, 클라이언트 리소스, 클라이언트 시크릿을 관리한다.
 */
@ConfigurationProperties(prefix = "keycloak")
@Configuration
@Data
public class KeycloakConfiguration {

	/** Keycloak 인증 서버 URL. */
	@Value("${keycloak.auth-server-url}")
	private String serverURL;
	/** Keycloak 렐름 이름. */
	private String realm;
	/** Keycloak 클라이언트 리소스 ID. */
	private String resource;
	/** Keycloak 클라이언트 시크릿. */
	@Value("${keycloak.credentials.secret}")
	private String clientSecret;




}
