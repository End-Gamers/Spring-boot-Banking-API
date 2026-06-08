# Spring Boot 뱅킹 API — 프로젝트 설명 및 사용 가이드

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [아키텍처](#아키텍처)
4. [실행 방법](#실행-방법)
5. [인증 방법](#인증-방법-keycloak-jwt)
6. [API 사용법](#api-사용법)
   - [고객 서비스](#1-고객-서비스)
   - [거래 서비스](#2-거래-서비스)
   - [대출 서비스](#3-대출-서비스)
   - [직원 서비스](#4-직원-서비스)
   - [불만사항 서비스](#5-불만사항-서비스)
7. [역할 및 권한](#역할-및-권한)
8. [Swagger UI](#swagger-ui-api-문서)
9. [주요 접속 URL](#주요-접속-url)
10. [로그 확인](#로그-확인)

---

## 프로젝트 개요

**Spring Boot 기반 마이크로서비스 뱅킹 REST API**입니다.

실제 은행 서비스처럼 고객 관리, 계좌 거래(입금·출금·송금), 대출 신청·승인·상환, 직원 관리, 불만사항 처리 기능을 제공합니다. 각 기능은 독립적인 마이크로서비스로 분리되어 있으며, API 게이트웨이를 통해 단일 진입점으로 접근합니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **프레임워크** | Spring Boot 2.6.15, Spring WebFlux |
| **마이크로서비스** | Spring Cloud Gateway, Eureka, OpenFeign |
| **인증/인가** | Keycloak 19, OAuth2, JWT |
| **데이터베이스** | MySQL 8, MongoDB, Redis |
| **메시지 브로커** | RabbitMQ |
| **내결함성** | Resilience4J (Circuit Breaker, Retry) |
| **모니터링** | Prometheus, Grafana, Zipkin |
| **컨테이너** | Docker, Kubernetes, Istio |
| **빌드** | Maven, Java 11 |

---

## 아키텍처

```
클라이언트 (브라우저 / Postman / 앱)
         │
         ▼
┌─────────────────────────────────┐
│     API Gateway  (포트 8765)    │  ← 단일 진입점 · JWT 인증 · Rate Limiting · XSS 필터 · 캐싱
└─────────────────────────────────┘
         │
         ├──────────┬──────────┬──────────┬──────────┐
         ▼          ▼          ▼          ▼          ▼
    Customer   Transaction   Loan    Employee    Issues
    (8081)      (8084)      (8083)   (8082)     (8085)
         │          │          │
         ▼          ▼          ▼
       MySQL     RabbitMQ   MongoDB
                 (비동기 메시지)
         │
         ▼
       Redis   ←  API Gateway 캐시
         │
         ▼
     Keycloak  ←  인증 서버 (포트 9090)
         │
         ▼
      Eureka   ←  서비스 디스커버리 (포트 8761)
         │
         ▼
  Config Server ←  중앙 설정 서버 (포트 8888)
```

### 서비스 간 통신
- **동기 (Synchronous)**: WebClient (Reactive) — 출금·송금 요청
- **비동기 (Asynchronous)**: RabbitMQ — 입금·이메일 발송·대출 분석·자동 상환

---

## 실행 방법

### 사전 요구사항

| 항목 | 버전 | 설치 방법 |
|------|------|-----------|
| Java | 11 | `brew install openjdk@11` |
| Maven | 3.x | `brew install maven` |
| MySQL | 8 | `brew install mysql` |
| RabbitMQ | - | `brew install rabbitmq` |
| Redis | - | `brew install redis` |
| MongoDB | - | `brew install mongodb/brew/mongodb-community` |
| Keycloak | 19.0.2 | [GitHub Releases](https://github.com/keycloak/keycloak/releases/tag/19.0.2) |

### 실행 순서

```bash
# 1. 인프라 서비스 시작
brew services start mysql
brew services start rabbitmq
brew services start redis
brew services start mongodb/brew/mongodb-community

# MySQL 비밀번호 설정 (최초 1회)
mysql -u root --skip-password -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'test1234';"

# 2. Keycloak 실행 (포트 9090)
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
KEYCLOAK_ADMIN=admin KEYCLOAK_ADMIN_PASSWORD=admin \
  ~/keycloak/keycloak-19.0.2/bin/kc.sh start-dev --http-port=9090 &

# 3. Common-dto 먼저 빌드 (다른 서비스가 의존)
cd Common-dto && mvn clean install

# 4. 마이크로서비스 빌드 (각 서비스 디렉터리에서)
mvn clean package -Dmaven.test.skip=true

# 5. 실행 순서 (순서 중요)
#   Config Server → Eureka → 나머지 서비스 → Api-gateway

java -jar Spring-cloud-config-server/target/*.jar &
java -jar Eureka-naming-server/target/*.jar &
java -jar Customer/target/customer-service.jar \
     --spring.profiles.active=dev \
     --spring.rabbitmq.listener.simple.missing-queues-fatal=false &
java -jar Transaction/target/*.jar --spring.profiles.active=dev \
     --spring.rabbitmq.listener.simple.missing-queues-fatal=false &
java -jar Loan/target/*.jar --spring.profiles.active=dev \
     --spring.rabbitmq.listener.simple.missing-queues-fatal=false &
java -jar Employee/target/*.jar --spring.profiles.active=dev &
java -jar Issues/target/*.jar --spring.profiles.active=dev &
java -jar Api-gateway/target/*.jar --spring.profiles.active=dev,redisCache &
```

---

## 인증 방법 (Keycloak JWT)

모든 API 요청에는 **Bearer Token**이 필요합니다.

### 1단계 — Keycloak에서 사용자 생성

1. [http://localhost:9090/admin](http://localhost:9090/admin) 접속 (admin / admin)
2. 좌측 상단 `master` 드롭다운 → `Springboot-bank-microservices` Realm 선택
3. `Users` → `Add User` → 사용자 생성
4. `Role Mappings` 탭에서 역할 부여 (`customer`, `admin` 등)

### 2단계 — 토큰 발급

```bash
curl -X POST \
  "http://localhost:9090/realms/Springboot-bank-microservices/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=springboot-bank" \
  -d "grant_type=password" \
  -d "username=<사용자명>" \
  -d "password=<비밀번호>"
```

**응답 예시:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5...",
  "expires_in": 300,
  "token_type": "Bearer"
}
```

### 3단계 — API 요청 시 헤더에 토큰 포함

```bash
curl -H "Authorization: Bearer <access_token>" \
  "http://localhost:8765/customer/api/customers"
```

### 참고 — 토큰만으로는 호출되지 않는 API가 있습니다 (역할 기반 권한)

대부분의 API는 **유효한 Bearer 토큰만 있으면** 호출할 수 있습니다 (`anyRequest().authenticated()`).
하지만 일부 엔드포인트는 토큰 검증을 통과해도 **JWT에 특정 역할(Role) 클레임이 없으면 403 Forbidden**이 발생합니다.

**동작 방식**

1. Keycloak이 발급한 JWT의 `realm_access.roles` 클레임에서 역할 목록을 추출 ([RealmRoleConverter.java](Customer/src/main/java/com/microfinanceBank/Customer/Config/RealmRoleConverter.java))
2. 각 역할 앞에 `ROLE_` 접두사를 붙여 Spring Security 권한(`GrantedAuthority`)으로 변환 (예: `admin` → `ROLE_admin`)
3. 컨트롤러의 `@RolesAllowed("...")` 어노테이션이 `ROLE_<값>`과 정확히 일치하는 권한을 요구 (대소문자 구분)

**역할이 추가로 필요한 엔드포인트**

| 엔드포인트 | 필요 역할 | 코드 위치 |
|---|---|---|
| `GET /customer/api/customers` (전체 고객 조회) | `ADMIN` | [CustomerController.java:61](Customer/src/main/java/com/microfinanceBank/Customer/controller/CustomerController.java#L61) |
| `GET /customer/api/accounts` (전체 계좌 조회) | `ADMIN` | [AccountController.java:85](Customer/src/main/java/com/microfinanceBank/Customer/controller/AccountController.java#L85) |
| `PUT /employee/api/make-admin/{id}` (관리자 승격) | `ADMIN` | [EmployeeController.java:66](Employee/src/main/java/com/microfinanceBank/Employee/controller/EmployeeController.java#L66) |
| `PUT /employee/api/demote-admin/{id}` (관리자 강등) | `ADMIN` | [EmployeeController.java:81](Employee/src/main/java/com/microfinanceBank/Employee/controller/EmployeeController.java#L81) |
| `POST /employee/api/employee` (직원 등록) | `hr` | [EmployeeController.java:35](Employee/src/main/java/com/microfinanceBank/Employee/controller/EmployeeController.java#L35) |
| `DELETE /employee/api/employee` (직원 삭제) | `hr` | [EmployeeController.java:51](Employee/src/main/java/com/microfinanceBank/Employee/controller/EmployeeController.java#L51) |

**주의 — 역할명 대소문자 불일치**

[SETUP.md](SETUP.md)의 Keycloak 역할 생성 스크립트는 역할명을 모두 소문자(`admin`, `customer`, `hr`, `manager`, `employee`)로 생성합니다. 그런데 위 표의 `ADMIN` 권한 검사(`@RolesAllowed("ADMIN")`)는 대문자로 되어 있어, Keycloak에서 변환된 권한 `ROLE_admin`(소문자)과 일치하지 않습니다.

→ `customers`/`accounts`/`make-admin`/`demote-admin` 엔드포인트를 호출하려면 Keycloak에 **대문자 `ADMIN`** 역할을 별도로 생성해 사용자에게 부여해야 합니다 (Role Mappings에서 `admin`이 아닌 `ADMIN` 역할을 추가). `hr` 역할을 요구하는 엔드포인트는 SETUP.md에서 생성한 소문자 `hr` 역할 그대로 사용하면 됩니다.

---

## API 사용법

> **Base URL**: `http://localhost:8765`
> 모든 요청에 `Authorization: Bearer <token>` 헤더 필요

---

### 1. 고객 서비스

**Base Path**: `/customer/api`

#### 고객 등록

```bash
POST /customer/api/customer
Content-Type: application/json

{
  "customer": {
    "firstName": "길동",
    "lastName": "홍",
    "email": "hong@example.com",
    "password": "비밀번호1234",
    "contactNumber": "010-1234-5678"
  },
  "accountType": "SAVINGS"
}
```

> `accountType`: `SAVINGS` (저축), `CHECKING` (당좌), `MONEY_MARKET` (머니마켓)

#### 계좌 개설

```bash
POST /customer/api/account
Content-Type: application/json

{
  "accountType": "SAVINGS",
  "customerId": 1
}
```

#### 계좌 조회

```bash
GET /customer/api/account/{계좌번호}
```

#### 직불카드 신청

```bash
POST /customer/api/debit-card
Content-Type: application/json

{
  "accountNumber": 12345678901,
  "cardType": "VISA"
}
```

#### 전체 고객 조회 (ADMIN 전용)

```bash
GET /customer/api/customers
```

---

### 2. 거래 서비스

**Base Path**: `/transaction/api`

#### 입금 (비동기 — RabbitMQ)

```bash
POST /transaction/api/deposit
Content-Type: application/json

{
  "sourceAccount": 12345678901,
  "amount": 100000,
  "description": "급여 입금"
}
```

> 응답: `202 Accepted` — 메시지 큐로 비동기 처리됩니다.

#### 출금

```bash
POST /transaction/api/withdraw
Content-Type: application/json

{
  "sourceAccount": 12345678901,
  "amount": 50000,
  "description": "생활비 출금"
}
```

#### 송금

```bash
POST /transaction/api/transfer
Content-Type: application/json

{
  "sourceAccount": 12345678901,
  "destinationAccount": 98765432101,
  "amount": 30000,
  "description": "친구에게 송금"
}
```

#### 거래 내역 조회

```bash
# 성공 입금 내역
GET /transaction/api/all-success-deposit

# 실패 입금 내역
GET /transaction/api/all-failed-deposit

# 계좌별 전체 거래 내역 (페이지네이션)
GET /transaction/api/all-customer-transaction?id={계좌번호}&offset=0&size=10
```

---

### 3. 대출 서비스

**Base Path**: `/loan/api`

#### 은행 대출 신청

```bash
POST /loan/api/bank-loan
Content-Type: application/json

{
  "borrowerAccountNumber": 12345678901,
  "principalLoanAmount": 5000000,
  "numberOfPayments": 12,
  "monthlyExpenses": 500000,
  "accountType": "SAVINGS",
  "joinDate": "2020-01-01",
  "haveAnExistingLoan": false,
  "borrowerDetails": {
    "occupation": "직장인",
    "email": "hong@example.com",
    "mobileNumber": "010-1234-5678",
    "monthlyIncome": 3000000,
    "maritalStatus": "SINGLE",
    "children": 0,
    "dob": "1990-05-15"
  },
  "loanOffer": {
    "interest": 5.5,
    "latePaymentInterest": 10.0
  }
}
```

#### P2P 대출 신청

```bash
POST /loan/api/p2p-loan
# 요청 본문은 은행 대출과 동일
```

#### 대출 승인 (ADMIN 전용)

```bash
PUT /loan/api/approve-loan?loan-id={대출ID}
```

#### 대출 상환

```bash
POST /loan/api/payment-loan
Content-Type: application/json

{
  "loanId": "abc123-...",
  "paymentAmount": 450000
}
```

---

### 4. 직원 서비스

**Base Path**: `/employee/api`

#### 직원 등록 (HR 전용)

```bash
POST /employee/api/employee
Content-Type: application/json

{
  "firstName": "민수",
  "lastName": "김",
  "email": "kim@bank.com",
  "password": "비밀번호1234",
  "role": "EMPLOYEE",
  "branch": 1
}
```

> `role`: `EMPLOYEE`, `HR`, `MANAGER`, `ADMIN`, `COO`

#### 직원 삭제 (HR 전용)

```bash
DELETE /employee/api/employee?id={직원ID}&keycloakId={keycloakId}
```

#### 관리자 승격 (ADMIN 전용)

```bash
PUT /employee/api/make-admin/{직원ID}
```

#### 관리자 강등 (ADMIN 전용)

```bash
PUT /employee/api/demote-admin/{직원ID}
```

#### 지점 생성

```bash
POST /employee/api/branch
Content-Type: application/json

{
  "address": {
    "street": "테헤란로 123",
    "city": "서울",
    "state": "서울특별시",
    "country": "대한민국",
    "postalCode": "06234"
  }
}
```

---

### 5. 불만사항 서비스

**Base Path**: `/issue/api`

#### 불만사항 접수

```bash
POST /issue/api/issue
Content-Type: application/json

{
  "accountNumber": 12345678901,
  "issue": "ATM 출금 중 오류가 발생했습니다."
}
```

#### 계좌별 불만사항 조회

```bash
GET /issue/api/issue?acc={계좌번호}
```

#### 전체 불만사항 조회 (SSE 스트림)

```bash
GET /issue/api/all-issues
# Accept: text/event-stream 으로 실시간 스트리밍
```

#### 처리 대기 불만사항 조회

```bash
GET /issue/api/pending-issues
```

#### 불만사항 처리 완료 (ADMIN 전용)

```bash
PUT /issue/api/issue-fix?id={불만사항ID}
```

---

## 역할 및 권한

| 역할 | 설명 | 주요 권한 |
|------|------|-----------|
| `customer` | 일반 고객 | 거래 요청, 대출 신청, 불만 접수 |
| `admin` | 시스템 관리자 | 대출 승인, 전체 고객/거래 조회, 관리자 승격 |
| `hr` | 인사 담당자 | 직원 등록·삭제 |
| `manager` | 부서 매니저 | 부서 관리 |
| `employee` | 일반 직원 | 기본 업무 |

> **참고**: 위 표는 [SETUP.md](SETUP.md)의 역할 생성 스크립트가 만드는 역할(전부 소문자) 기준입니다.
> 다만 일부 ADMIN 전용 엔드포인트는 코드상 대문자 `ADMIN` 권한을 요구하므로, 해당 기능을 사용하려면
> 별도로 대문자 `ADMIN` 역할을 만들어 부여해야 합니다 — 자세한 내용은
> [API 사용법의 "역할 기반 권한" 안내](#참고--토큰만으로는-호출되지-않는-api가-있습니다-역할-기반-권한)를 참고하세요.

### Keycloak에서 사용자 역할 부여 방법

```
1. http://localhost:9090/admin 접속
2. Springboot-bank-microservices Realm 선택
3. Users → 사용자 선택 → Role Mappings
4. Available Roles에서 원하는 역할 선택 → Add Selected
```

---

## Swagger UI (API 문서)

브라우저에서 API를 직접 테스트할 수 있습니다.

```
http://localhost:8765/swagger-ui.html
```

Swagger UI에서 **Authorize** 버튼을 클릭하고 발급받은 Bearer Token을 입력하면 모든 API를 테스트할 수 있습니다.

---

## 주요 접속 URL

| 서비스 | URL | 설명 |
|--------|-----|------|
| **API Gateway** | http://localhost:8765 | 모든 API 요청의 진입점 |
| **Swagger UI** | http://localhost:8765/swagger-ui.html | API 문서 및 테스트 |
| **Eureka Dashboard** | http://localhost:8761 | 서비스 등록 현황 |
| **Keycloak Admin** | http://localhost:9090/admin | 사용자·역할 관리 (admin/admin) |
| **Keycloak Realm** | http://localhost:9090/admin/Springboot-bank-microservices/console/ | Realm 직접 접속 |
| **RabbitMQ 관리** | http://localhost:15672 | 메시지 큐 모니터링 (guest/guest) |
| **Config Server** | http://localhost:8888 | 중앙 설정 서버 |

---

## 로그 확인

모든 마이크로서비스는 콘솔 출력 외에 파일로도 로그를 저장하며(`logback-spring.xml`), 분산 추적용 상관관계 ID를 함께 기록합니다.

### 로그 파일 위치

각 서비스 디렉터리 아래 `logs/{spring.application.name}/`에 텍스트 로그와 JSON 구조화 로그가 함께 생성됩니다 (일별 + 100MB 단위 자동 롤링, 30일 보관, 총 3GB 캡).

```
{서비스 디렉터리}/logs/{서비스명}/
├── {서비스명}.log         # 텍스트 로그 (콘솔과 동일한 패턴)
└── {서비스명}-json.log    # JSON 구조화 로그 (ELK/Loki 적재용 — LogstashEncoder)
```

예: `Customer/logs/customer/customer.log`, `Api-gateway/logs/api-gateway/api-gateway-json.log`

### 분산 추적 — traceId / spanId

API Gateway를 거쳐 백엔드 서비스로 이어지는 요청은 Spring Cloud Sleuth(Brave)가 부여한 **`traceId`/`spanId`**로 서로 연결됩니다. 모든 액세스 로그(텍스트·JSON)에 이 값이 함께 기록되므로, 동일한 `traceId`로 검색하면 게이트웨이부터 다운스트림 서비스까지 하나의 요청 흐름을 추적할 수 있습니다.

```bash
# 텍스트 로그 패턴: [traceId,spanId]
grep "31d8a7fe8ae48e03" Api-gateway/logs/api-gateway/api-gateway.log Customer/logs/customer/customer.log

# JSON 로그는 jq로 traceId 기준 필터링
jq 'select(.traceId == "31d8a7fe8ae48e03")' Customer/logs/customer/customer-json.log
```

### 액세스 로그 형식

요청 메서드·경로·응답 상태코드·처리 시간을 기록합니다.

```
GET /customer/api/customers -> 200 OK (12ms)
```

---

## 자주 발생하는 오류

| 오류 | 원인 | 해결 방법 |
|------|------|-----------|
| `401 Unauthorized` | 토큰 없음 또는 만료 | 토큰 재발급 후 요청 |
| `403 Forbidden` | 권한 부족 | Keycloak에서 역할 확인 |
| `503 Service Unavailable` | 서비스 다운 | 해당 서비스 재시작 |
| `잔액이 부족합니다.` | 계좌 잔액 부족 | 먼저 입금 후 재시도 |
| `계좌가 비활성 상태입니다.` | 계좌 비활성화 | 관리자에게 계좌 활성화 요청 |

---

*문의사항은 GitHub Issues를 통해 등록해 주세요.*
