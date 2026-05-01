package com.microfinanceBank.apigateway.model;

import lombok.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;

/**
 * 캐시 키로 사용되는 HTTP 요청 정보 모델.
 * 경로(path), HTTP 메서드(method), 쿼리 파라미터(queryParams)를 조합하여 고유 키를 생성한다.
 */
@Builder
@Data
//@Value
@NoArgsConstructor
@AllArgsConstructor
public  class CachedRequest implements Serializable{
    /** 요청 경로. */
    RequestPath path;
    /** HTTP 메서드 (GET, POST 등). */
    HttpMethod method;
    /** 쿼리 파라미터 맵. */
    MultiValueMap<String, String> queryParams;



}
