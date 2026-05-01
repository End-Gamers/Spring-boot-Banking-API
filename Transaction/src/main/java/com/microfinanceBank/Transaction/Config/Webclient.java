package com.microfinanceBank.Transaction.Config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * 프로파일별 WebClient 빌더 설정 클래스.
 * dev 환경에서는 로드 밸런싱이 적용된 WebClient를, prod 환경에서는 일반 WebClient를 제공한다.
 */
@Configuration
public class Webclient {

    /** 개발 환경(dev): Eureka 로드 밸런싱이 적용된 WebClient.Builder를 등록한다. */
    @Profile("dev")
    public static class Dev {
        @Bean
        @LoadBalanced
        public WebClient.Builder webClient() {
            return WebClient.builder();
        }
    }

    /** 운영 환경(prod): 로드 밸런싱 없이 일반 WebClient.Builder를 등록한다. */
    @Profile("prod")
    public static class Prod {
        @Bean
        public WebClient.Builder webClient() {
            return WebClient.builder();
        }
    }



}
