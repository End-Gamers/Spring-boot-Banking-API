# 로컬 개발 환경 설정 가이드

> **작성 기준**: macOS (Apple Silicon / arm64) 에서 실제로 동작 확인된 설정입니다.
> 이 문서를 순서대로 따라하면 프로젝트를 재현할 수 있습니다.

---

## 목차
1. [검증된 환경 사양](#검증된-환경-사양)
2. [사전 준비 — Homebrew 설치](#1단계-사전-준비--homebrew-설치)
3. [인프라 설치 및 시작](#2단계-인프라-설치-및-시작)
4. [Keycloak 설치 및 설정](#3단계-keycloak-설치-및-설정)
5. [소스 빌드](#4단계-소스-빌드)
6. [설정 파일 수정](#5단계-설정-파일-수정)
7. [서비스 실행](#6단계-서비스-실행)
8. [재기동 방법](#7단계-재기동-방법)
9. [트러블슈팅](#트러블슈팅)

---

## 검증된 환경 사양

| 항목 | 버전 |
|------|------|
| **OS** | macOS (Apple Silicon, arm64) |
| **Java** | OpenJDK 11.0.31 (Homebrew) |
| **Maven** | 3.9.16 |
| **MySQL** | 9.6.0 |
| **RabbitMQ** | 4.3.0 |
| **Erlang** | 28.5 (RabbitMQ 의존성) |
| **Redis** | 8.6.3 |
| **MongoDB** | 8.2.9 |
| **Keycloak** | 19.0.2 |

> **중요**: Keycloak 19는 **Java 21과 호환되지 않습니다.** 반드시 Java 11을 사용해야 합니다.

---

## 1단계: 사전 준비 — Homebrew 설치

```bash
# Homebrew 설치 (이미 설치된 경우 생략)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

---

## 2단계: 인프라 설치 및 시작

### 패키지 일괄 설치

```bash
# Java 11
brew install openjdk@11

# Maven
brew install maven

# MySQL, RabbitMQ, Redis
brew install mysql rabbitmq redis

# MongoDB (별도 탭 추가 필요)
brew tap mongodb/brew
brew install mongodb/brew/mongodb-community

# RabbitMQ 의존성 Erlang 링크
brew link erlang --overwrite
```

### JAVA_HOME 설정

매번 터미널을 열 때마다 Java 11을 사용하려면 `~/.zshrc`에 추가합니다:

```bash
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@11"' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

> 이 설정이 없으면 매 실행 시마다 아래 명령어로 직접 지정해야 합니다:
> ```bash
> export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
> export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:$PATH"
> ```

### 인프라 서비스 시작

```bash
brew services start mysql
brew services start rabbitmq
brew services start redis
brew services start mongodb/brew/mongodb-community
```

### MySQL root 비밀번호 설정 (최초 1회)

```bash
# 비밀번호 없이 접속하여 설정
mysql -u root --skip-password -e \
  "ALTER USER 'root'@'localhost' IDENTIFIED BY 'test1234'; FLUSH PRIVILEGES;"
```

> 프로젝트 설정 파일의 DB 비밀번호는 `test1234`로 고정되어 있습니다.

### 서비스 시작 확인

```bash
brew services list
```

| 서비스 | 포트 | 기대 상태 |
|--------|------|-----------|
| MySQL | 3306 | started |
| RabbitMQ | 5672 | started |
| Redis | 6379 | started |
| MongoDB | 27017 | started |

---

## 3단계: Keycloak 설치 및 설정

### 다운로드 및 압축 해제

```bash
mkdir -p ~/keycloak
curl -L "https://github.com/keycloak/keycloak/releases/download/19.0.2/keycloak-19.0.2.zip" \
  -o ~/keycloak/keycloak-19.0.2.zip
cd ~/keycloak && unzip keycloak-19.0.2.zip
```

### Keycloak 시작

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
export PATH="$JAVA_HOME/bin:$PATH"

# admin 계정(admin/admin)으로 개발 모드 실행 (포트 9090)
KEYCLOAK_ADMIN=admin KEYCLOAK_ADMIN_PASSWORD=admin \
  nohup ~/keycloak/keycloak-19.0.2/bin/kc.sh start-dev --http-port=9090 \
  > ~/keycloak/keycloak.log 2>&1 &

# 기동 완료 대기
until curl -s http://localhost:9090/realms/master > /dev/null 2>&1; do sleep 3; done
echo "Keycloak 기동 완료"
```

### Realm 및 클라이언트 자동 설정

Keycloak이 기동된 후 아래 스크립트를 실행합니다:

```bash
# admin 토큰 발급
KC_TOKEN=$(curl -s -X POST \
  "http://localhost:9090/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin&grant_type=password&client_id=admin-cli" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

# Realm 생성
curl -s -X POST "http://localhost:9090/admin/realms" \
  -H "Authorization: Bearer $KC_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"realm":"Springboot-bank-microservices","enabled":true}'

# 역할 생성
for ROLE in admin customer hr manager employee; do
  curl -s -X POST \
    "http://localhost:9090/admin/realms/Springboot-bank-microservices/roles" \
    -H "Authorization: Bearer $KC_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"$ROLE\"}"
done

# springboot-bank 클라이언트 생성
curl -s -X POST \
  "http://localhost:9090/admin/realms/Springboot-bank-microservices/clients" \
  -H "Authorization: Bearer $KC_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "springboot-bank",
    "enabled": true,
    "publicClient": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "secret": "V8KI3Go8THYRRkZd6mmnvMQKRevzrpKY",
    "redirectUris": ["*"],
    "webOrigins": ["*"]
  }'

# idm-client 생성 (직원 서비스용)
curl -s -X POST \
  "http://localhost:9090/admin/realms/Springboot-bank-microservices/clients" \
  -H "Authorization: Bearer $KC_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "idm-client",
    "enabled": true,
    "publicClient": false,
    "serviceAccountsEnabled": true,
    "secret": "Ej90dV9fdLS0IOnBktvkPw5Hlje6wmvi",
    "directAccessGrantsEnabled": true
  }'

echo "Keycloak Realm/클라이언트 설정 완료"
```

### 테스트 사용자 생성 (선택)

```bash
KC_TOKEN=$(curl -s -X POST \
  "http://localhost:9090/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin&grant_type=password&client_id=admin-cli" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

# 고객 테스트 계정 생성 (user1 / password1)
curl -s -X POST \
  "http://localhost:9090/admin/realms/Springboot-bank-microservices/users" \
  -H "Authorization: Bearer $KC_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "email": "user1@test.com",
    "enabled": true,
    "credentials": [{"type":"password","value":"password1","temporary":false}]
  }'
```

---

## 4단계: 소스 빌드

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:$PATH"
BASE="/Users/seojeongchang/VSProject/Spring-boot-Banking-API"

# 1. 공유 DTO 먼저 빌드 (다른 서비스가 의존)
cd "$BASE/Common-dto"
mvn clean install

# 2. 나머지 서비스 빌드
for service in Spring-cloud-config-server Eureka-naming-server Customer Transaction Loan Employee Issues Api-gateway; do
  echo "=== $service 빌드 중 ==="
  cd "$BASE/$service"
  mvn clean package -Dmaven.test.skip=true
done

echo "전체 빌드 완료"
```

> **주의**: `-Dmaven.test.skip=true`는 테스트 컴파일·실행을 모두 생략합니다.
> 테스트 코드 일부에 Common-dto 버전 불일치 오류가 있어 사용합니다.

---

## 5단계: 설정 파일 수정

### Config Server — 로컬 Git 저장소 사용으로 변경

파일 경로: `Spring-cloud-config-server/src/main/resources/application.properties`

```properties
spring.application.name=spring-cloud-config-server
server.port=8888

# 로컬 git 저장소 사용 (원격 GitHub 대신)
spring.cloud.config.server.git.cloneOnStart=true
spring.cloud.config.server.git.uri=file:///Users/seojeongchang/VSProject/Spring-boot-Banking-API/git-localconfig-repo
spring.cloud.config.server.git.default-label=main

server.tomcat.max-threads=1
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```

> **변경 이유**: 원래 설정은 원격 GitHub 저장소(`phelumie/spring-cloud-config`)를 바라보고 있어
> 외부 인터넷이 없거나 권한이 없는 경우 작동하지 않습니다.

### git-localconfig-repo — Git 저장소 초기화 (최초 1회)

Config Server가 로컬 디렉터리를 Git으로 읽기 때문에 초기화가 필요합니다:

```bash
cd /Users/seojeongchang/VSProject/Spring-boot-Banking-API/git-localconfig-repo
git init
git add .
git commit -m "init config"
```

> 이미 완료된 상태라면 이 단계는 생략합니다.

---

## 6단계: 서비스 실행

### 실행 순서 (반드시 준수)

```
1. Config Server  →  2. Eureka  →  3. 앱 서비스들  →  4. Api-gateway
```

### 실행 스크립트

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:$PATH"
BASE="/Users/seojeongchang/VSProject/Spring-boot-Banking-API"

# 1. Config Server
nohup java -jar "$BASE/Spring-cloud-config-server/target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar" \
  > /tmp/config-server.log 2>&1 &
echo "Config Server 시작..."
until curl -s http://localhost:8888/actuator/health | grep -q "UP"; do sleep 2; done
echo "Config Server 완료 ✓"

# 2. Eureka
nohup java -jar "$BASE/Eureka-naming-server/target/eureka-naming-server-0.0.1-SNAPSHOT.jar" \
  > /tmp/eureka.log 2>&1 &
echo "Eureka 시작..."
until curl -s http://localhost:8761/actuator/health | grep -q "UP"; do sleep 2; done
echo "Eureka 완료 ✓"

# 3. 앱 서비스 (동시 시작)
for svc in Customer Transaction Loan Employee Issues; do
  jar=$(ls "$BASE/$svc/target/"*.jar | grep -v original | head -1)
  nohup java -jar "$jar" \
    --spring.profiles.active=dev \
    --spring.cloud.config.uri=http://localhost:8888 \
    --spring.rabbitmq.listener.simple.missing-queues-fatal=false \
    > /tmp/$(echo $svc | tr '[:upper:]' '[:lower:]').log 2>&1 &
  echo "$svc 시작 (PID: $!)"
done

# 4. Api-gateway (redisCache 프로파일 포함)
nohup java -jar "$BASE/Api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active=dev,redisCache \
  --spring.cloud.config.uri=http://localhost:8888 \
  > /tmp/api-gateway.log 2>&1 &
echo "Api-gateway 시작 (PID: $!)"
```

### 기동 완료 확인

```bash
# 전체 서비스 포트 확인
for label_port in "Config Server:8888" "Eureka:8761" "Customer:8081" "Transaction:8084" "Loan:8085" "Api-gateway:8765"; do
  label=$(echo $label_port | cut -d: -f1)
  port=$(echo $label_port | cut -d: -f2)
  lsof -i :$port 2>/dev/null | grep -q LISTEN \
    && echo "  $label ✓" \
    || echo "  $label ✗ (아직 기동 중)"
done
```

---

## 7단계: 재기동 방법

프로젝트를 다시 실행해야 할 때의 순서입니다.

### 인프라 서비스 확인 및 시작

```bash
brew services list
# 중지된 서비스가 있으면:
brew services start mysql
brew services start rabbitmq
brew services start redis
brew services start mongodb/brew/mongodb-community
```

### Keycloak 재시작

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
export PATH="$JAVA_HOME/bin:$PATH"

# 기존 프로세스 종료
pkill -f "keycloak-19" 2>/dev/null || true

# 재시작
KEYCLOAK_ADMIN=admin KEYCLOAK_ADMIN_PASSWORD=admin \
  nohup ~/keycloak/keycloak-19.0.2/bin/kc.sh start-dev --http-port=9090 \
  > ~/keycloak/keycloak.log 2>&1 &

until curl -s http://localhost:9090/realms/master > /dev/null 2>&1; do sleep 3; done
echo "Keycloak ✓"
```

### Spring Boot 서비스 재기동

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:$PATH"
BASE="/Users/seojeongchang/VSProject/Spring-boot-Banking-API"

# 기존 서비스 종료
pkill -f "customer-service|Transaction-0|Loan-0|Employee-0|Issues-0|api-gateway-0|spring-cloud-config-server|eureka-naming-server" 2>/dev/null || true
sleep 3

# Config Server
nohup java -jar "$BASE/Spring-cloud-config-server/target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar" \
  > /tmp/config-server.log 2>&1 &
until curl -s http://localhost:8888/actuator/health | grep -q "UP"; do sleep 2; done
echo "Config Server ✓"

# Eureka
nohup java -jar "$BASE/Eureka-naming-server/target/eureka-naming-server-0.0.1-SNAPSHOT.jar" \
  > /tmp/eureka.log 2>&1 &
until curl -s http://localhost:8761/actuator/health | grep -q "UP"; do sleep 2; done
echo "Eureka ✓"

# Customer
nohup java -jar "$BASE/Customer/target/customer-service.jar" \
  --spring.profiles.active=dev \
  --spring.cloud.config.uri=http://localhost:8888 \
  --spring.rabbitmq.listener.simple.missing-queues-fatal=false \
  > /tmp/customer.log 2>&1 &
echo "Customer 시작 (PID: $!)"

# Transaction
nohup java -jar "$BASE/Transaction/target/Transaction-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active=dev \
  --spring.cloud.config.uri=http://localhost:8888 \
  --spring.rabbitmq.listener.simple.missing-queues-fatal=false \
  > /tmp/transaction.log 2>&1 &
echo "Transaction 시작 (PID: $!)"

# Loan
nohup java -jar "$BASE/Loan/target/Loan-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active=dev \
  --spring.cloud.config.uri=http://localhost:8888 \
  --spring.rabbitmq.listener.simple.missing-queues-fatal=false \
  > /tmp/loan.log 2>&1 &
echo "Loan 시작 (PID: $!)"

# Employee
nohup java -jar "$BASE/Employee/target/Employee-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active=dev \
  --spring.cloud.config.uri=http://localhost:8888 \
  --spring.rabbitmq.listener.simple.missing-queues-fatal=false \
  > /tmp/employee.log 2>&1 &
echo "Employee 시작 (PID: $!)"

# Issues
nohup java -jar "$BASE/Issues/target/Issues-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active=dev \
  --spring.cloud.config.uri=http://localhost:8888 \
  --spring.rabbitmq.listener.simple.missing-queues-fatal=false \
  > /tmp/issues.log 2>&1 &
echo "Issues 시작 (PID: $!)"

# Api-gateway
nohup java -jar "$BASE/Api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active=dev,redisCache \
  --spring.cloud.config.uri=http://localhost:8888 \
  > /tmp/api-gateway.log 2>&1 &
echo "Api-gateway 시작 (PID: $!)"

echo ""
echo "모든 서비스 시작 완료. 기동에 약 2~3분 소요됩니다."
echo "로그 확인: tail -f /tmp/customer.log"
```

---

## 서비스 포트 요약

| 서비스 | 포트 | 비고 |
|--------|------|------|
| **Api-gateway** | **8765** | 모든 외부 요청 진입점 |
| Config Server | 8888 | 설정 중앙 관리 |
| Eureka | 8761 | 서비스 디스커버리 |
| Customer | 8081 | 고객·계좌 관리 |
| Transaction | 8084 | 입금·출금·송금 |
| Loan | 8085 | 대출 신청·상환 |
| Employee | 8082 | 직원·지점 관리 |
| Issues | 8083 | 불만사항 관리 |
| Keycloak | 9090 | 인증 서버 |
| MySQL | 3306 | 관계형 DB |
| RabbitMQ | 5672 | 메시지 브로커 |
| RabbitMQ 관리 UI | 15672 | guest / guest |
| Redis | 6379 | 캐시 |
| MongoDB | 27017 | Issues 서비스 DB |

---

## 주요 접속 URL

| 목적 | URL |
|------|-----|
| API 게이트웨이 | http://localhost:8765 |
| Swagger UI | http://localhost:8765/swagger-ui.html |
| Eureka 대시보드 | http://localhost:8761 |
| Keycloak 관리자 | http://localhost:9090/admin (admin / admin) |
| Keycloak Realm | http://localhost:9090/admin/Springboot-bank-microservices/console/ |
| RabbitMQ 관리 | http://localhost:15672 (guest / guest) |

---

## 트러블슈팅

### RabbitMQ가 시작되지 않음

```bash
# Erlang 링크 재설정
brew link erlang --overwrite
brew services restart rabbitmq
```

> **원인**: Erlang이 설치는 됐지만 PATH에 링크되지 않은 경우 발생합니다.

---

### Config Server 404 오류 (`/customer/dev` not found)

```bash
# git-localconfig-repo의 브랜치 확인
cd /Users/seojeongchang/VSProject/Spring-boot-Banking-API/git-localconfig-repo
git branch
```

브랜치가 `main`이 아닌 `master`라면 `application.properties`를 수정:
```properties
spring.cloud.config.server.git.default-label=main  # 또는 master
```

> **원인**: Git 2.x 이후 기본 브랜치가 `master`에서 `main`으로 변경되었습니다.

---

### 서비스 기동 실패 — `QueuesNotAvailableException`

```
cannot find queue 'DepositQueue' / 'LoanWithdrawQueue'
```

```bash
# missing-queues-fatal=false 옵션 추가 필수
java -jar xxx.jar \
  --spring.rabbitmq.listener.simple.missing-queues-fatal=false
```

> **원인**: 서비스 시작 시 상대 서비스가 선언하는 큐가 아직 생성되지 않아 발생합니다.
> 이 옵션을 추가하면 큐가 없어도 서비스가 정상 기동되고, 이후 큐가 생성되면 자동으로 연결됩니다.

---

### Keycloak 기동 실패 — Java 버전 오류

```
Java 21 is not supported by the current version of Byte Buddy
```

```bash
# Java 11로 실행해야 함
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
export PATH="$JAVA_HOME/bin:$PATH"
~/keycloak/keycloak-19.0.2/bin/kc.sh start-dev --http-port=9090
```

> **원인**: Keycloak 19.0.2는 최대 Java 19까지만 공식 지원합니다.

---

### 빌드 실패 — Java 21 / Lombok 호환성 오류

```
Fatal error compiling: java.lang.NoSuchFieldError: Class JCTree$JCImport does not have member field 'qualid'
```

```bash
# Java 11로 빌드해야 함
export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
mvn clean package -Dmaven.test.skip=true
```

---

### Customer/Transaction 빌드 실패 — Common-dto 심볼 불일치

소스 코드와 Common-dto 클래스 간 필드명 불일치로 발생했습니다. 이미 수정된 사항입니다:

| 파일 | 수정 내용 |
|------|-----------|
| `BankServiceImpl.java` | `TransactionStatus.ERROR` → `TransactionStatus.FAILED` |
| `BankServiceImpl.java` | `transfer.getRecipientAccount()` → `transfer.getDestinationAccount()` |
| `GenerateTransactionDetailsQueueImpl.java` | `queue.setWithdraw()` → `queue.setWithdrawal()` |
| `CustomerServiceTransactionListener.java` | `withdrawDto.getWithdraw()` → `withdrawDto.getWithdrawal()` |
| `ResourceServerConfig.java` (Customer) | Git 병합 충돌 마커 제거 |

---

*마지막 업데이트: 2026-05-20*
