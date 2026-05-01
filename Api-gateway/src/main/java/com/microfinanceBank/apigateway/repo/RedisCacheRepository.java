package com.microfinanceBank.apigateway.repo;

import com.microfinanceBank.apigateway.model.CachedRequest;
import com.microfinanceBank.apigateway.model.CachedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Reactive Redis 기반 캐시 저장소 (redisCache 프로파일 전용).
 * HTTP 응답을 CachedRequest 키로 저장하고 조회하는 기능을 제공한다.
 */
@Slf4j
@Repository
@Profile("redisCache")
public class RedisCacheRepository {

    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, CachedResponse> cache;

    public RedisCacheRepository(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, CachedResponse> cache) {
        this.factory = factory;
        this.cache = cache;
    }

    /**
     * 요청 키에 해당하는 캐시된 응답을 조회한다.
     *
     * @param cachedRequest 캐시 키 객체
     * @return 캐시된 CachedResponse Flux
     */
    public Flux<CachedResponse> get(CachedRequest cachedRequest) {
        return cache.keys(cachedRequest.toString())
                .flatMap(cache.opsForValue()::get);
    }

    /**
     * 응답을 Redis에 30분간 저장한다. 저장 전 bgSave 명령으로 스냅샷을 트리거한다.
     *
     * @param cacheKey      캐시 키 객체
     * @param cachedResponse 저장할 응답 객체
     * @return 저장 성공 여부 Flux
     */
    public Flux<Boolean> put(CachedRequest cacheKey, CachedResponse cachedResponse) {
        return factory.getReactiveConnection().serverCommands().bgSave().thenMany(
                Flux.just(cachedResponse)
                        .flatMap(data -> cache.opsForValue().set(cacheKey.toString(), cachedResponse, Duration.ofMinutes(30)))
        );

    }

}
