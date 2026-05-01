package com.microfinanceBank.apigateway.service;

import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * API 속도 제한 관련 캐시 조작을 정의하는 서비스 인터페이스.
 * 사용자 ID별 API 호출 횟수를 Redis에 저장하고 조회한다.
 */
public interface RateLimiterService {
     /**
      * 사용자의 현재 API 호출 횟수를 캐시에서 조회한다.
      * @param id 사용자 식별자 (Keycloak ID + 서비스명)
      * @return 현재 호출 횟수
      */
     Mono<Integer> getApiHitCount(String id);

     /**
      * 사용자의 API 호출 횟수를 1 증가시킨다.
      * @param id 사용자 식별자
      * @return 증가 후 값
      */
     Mono<Long> incrementApiHitCount(String id);

     /**
      * 사용자의 API 호출 횟수를 초기화(1로 설정)하고 만료 시간을 지정한다.
      * @param id 사용자 식별자
      * @return 설정 성공 여부
      */
     Mono<Boolean> put(String id);
//     String getApiHitCount(String userId);


//     void incrementApiHitCount(String userId);


}