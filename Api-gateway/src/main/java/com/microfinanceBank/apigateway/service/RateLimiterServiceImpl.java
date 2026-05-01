package com.microfinanceBank.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * RateLimiterService 구현체 (redisCache 프로파일 전용).
 * Reactive Redis를 사용하여 사용자별 API 호출 횟수를 관리한다.
 */
@Slf4j
@Profile("redisCache")
public class RateLimiterServiceImpl implements RateLimiterService {

    private final ReactiveRedisTemplate<String,String> redisTemplate;
    private final ReactiveValueOperations<String,String> ops;

    public RateLimiterServiceImpl(ReactiveRedisTemplate<String, String> redisTemplate, ReactiveValueOperations<String, String> ops) {
        this.redisTemplate = redisTemplate;
        this.ops = ops;
        ops=redisTemplate.opsForValue();
    }


    /** Redis에서 사용자의 현재 API 호출 횟수를 조회한다. */
    @Override
    public Mono<Integer> getApiHitCount(String id) {
        return ops.get(id).map(digit->Integer.valueOf(digit));

    }

    /** 사용자의 API 호출 횟수를 1 증가시킨다. */
    @Override
    public Mono<Long> incrementApiHitCount(String id) {
        return ops.increment(id);

    }

    /** 사용자의 호출 횟수를 "1"로 초기화하고 1초 TTL을 설정한다. */
    @Override
    public Mono<Boolean> put(String id) {
        return ops.set(id,"1", Duration.ofSeconds(1));

    }
//    @Override
//    public void incrementApiHitCount(String userId) {
//        template.
//                opsForValue().
//                increment("customer" + "::" + userId);
//    }
}
