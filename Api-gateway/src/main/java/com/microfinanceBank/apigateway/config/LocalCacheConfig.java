package com.microfinanceBank.apigateway.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

/**
 * 로컬 인메모리 캐시 설정 클래스 (localCache 프로파일 전용).
 * Redis 없이 JVM 내부의 ConcurrentMap 기반 캐시를 사용한다.
 */
@Configuration
@Profile("localCache")
public class LocalCacheConfig {

	/**
	 * ConcurrentMap 기반 CacheManager 빈을 등록한다.
	 * null 값은 허용하지 않으며, "MyCache" 라는 단일 캐시 영역을 사용한다.
	 */
	@Bean
	public CacheManager cacheManager(){
		ConcurrentMapCacheManager cacheManager=new ConcurrentMapCacheManager();
		cacheManager.setAllowNullValues(false);
		cacheManager.setCacheNames(Collections.singleton("MyCache"));
		return cacheManager;
	}



}

