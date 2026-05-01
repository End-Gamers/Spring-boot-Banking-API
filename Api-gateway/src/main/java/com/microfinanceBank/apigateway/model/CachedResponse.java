package com.microfinanceBank.apigateway.model;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Redis에 저장되는 HTTP 응답 캐시 모델.
 * HTTP 상태 코드, 응답 헤더, 응답 본문(바이트 배열)을 보관한다.
 */
@Data
//@TypeAlias("response")
//@Value
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("MyCache")
public class CachedResponse implements Serializable {

    /** HTTP 응답 상태 코드. */
    HttpStatus httpStatus;
    /** 응답 헤더 맵 (헤더명 → 헤더값). */
    Map<String,String> headers;
    /** 직렬화된 응답 본문. */
    byte[] body;

}


