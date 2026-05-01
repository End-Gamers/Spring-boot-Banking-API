package com.microfinanceBank.Employee.service;

import com.microfinanceBank.Employee.dto.EmployeeDto;
import com.microfinanceBank.Employee.dto.RegisterDto;
import com.microfinanceBank.Employee.dto.RegisterResponse;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.List;

/**
 * 직원 관리 서비스 인터페이스.
 * 직원 등록·삭제·역할 변경·조회 및 로그인 이력 관련 기능을 정의한다.
 */
public interface EmployeeService {
    /** 새 직원을 등록하고 Keycloak 계정을 생성한다. */
    RegisterResponse registerEmployee(RegisterDto user);
    /** 현재 로그인한 사용자의 Keycloak ID를 반환한다. */
    String getCurrentUserLogin();
    /** 특정 직원을 ADMIN 역할로 승격한다. */
    RegisterResponse makeAdmin(Long id);
    /** ADMIN을 일반 직원으로 강등한다. */
    RegisterResponse demoteAdminToEmployee(Long id);
    /** 전체 직원 목록을 Flux 스트림으로 반환한다. */
    Flux<EmployeeDto> getAllUsersFlux();
    /** 전체 직원 목록을 List로 반환한다. */
    List<EmployeeDto> getAllEmployeeList();
    /** 특정 직원의 마지막 로그인 날짜를 반환한다. */
    Date getEmployeeLastLoginDate(String keycloakId);
    /** 특정 직원의 모든 로그인 이력 날짜 목록을 반환한다. */
    List<Date> getAllEmployeeLoginSessions(String keycloakId);
    /** 현재 활성 세션을 가진 직원 목록을 반환한다. */
    List<EmployeeDto> allActiveSessions();
    /** 직원을 DB와 Keycloak에서 모두 삭제한다. */
    void deleteEmployee(Long id,String keycloakId);
}