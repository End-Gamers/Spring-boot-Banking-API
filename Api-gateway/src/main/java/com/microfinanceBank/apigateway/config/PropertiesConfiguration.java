package com.microfinanceBank.apigateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * "cache" 접두사를 가진 외부 설정 프로퍼티를 바인딩하는 설정 클래스.
 * application.properties / application.yml 의 cache.* 값을 필드에 주입한다.
 */
@ConfigurationProperties(prefix = "cache")
@Configuration
@Data
public class PropertiesConfiguration {
	/** Redis 서버 호스트 주소 (cache.redisHost 프로퍼티). */
	private String redisHost;


}
