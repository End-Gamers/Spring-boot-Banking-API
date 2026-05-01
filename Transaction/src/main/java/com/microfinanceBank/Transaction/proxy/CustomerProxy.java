package com.microfinanceBank.Transaction.proxy;

import com.microfinanceBank.Transaction.Config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

/**
 * Customer 서비스와 통신하는 OpenFeign 클라이언트 인터페이스.
 * FeignClientInterceptor를 통해 JWT 토큰을 자동으로 전달한다.
 */
@FeignClient(name = "customer",configuration = FeignClientInterceptor.class)
//@LoadBalancerClient(name = "customer",configuration= LoadBalancerConfiguration.class)
public interface CustomerProxy {
//    @GetMapping("api/customer/{accountNumber}")
//    public ResponseEntity<Optional<Customer>> getCustomerByAccountNumber(@PathVariable Long accountNumber);


    }