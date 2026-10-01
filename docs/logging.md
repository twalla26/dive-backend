# Logging 전략

## 목적

* **요청 추적**: 하나의 요청에서 발생한 로그를 흩어지지 않게 묶어서 본다.
* **문제 파악**: 어떤 요청이 느렸고, 어떤 요청이 실패했는지 로그만 보고 판단한다.

## 요청 로그 (Access Log)

`RequestLoggingFilter`(`OncePerRequestFilter`)가 모든 요청의 처리 결과를 한 줄로 남깁니다.

* **등록 순서**: `@Order(Ordered.HIGHEST_PRECEDENCE)` — 가장 앞단에서 동작해야 인터셉터/컨트롤러에서 발생한 로그까지 모두 포함할 수 있습니다.
* **제외 대상**: `/actuator/**` — 헬스 체크가 주기적으로 호출되며 로그를 오염시키므로 제외합니다.
* **기록 시점**: `finally` 블록 — 예외가 터져도 요청 로그는 반드시 남깁니다.

### 기록 항목

| 항목             | 설명                                              |
|:---------------|:------------------------------------------------|
| `method`       | HTTP 메서드                                        |
| `requestURI`   | 요청 경로 (쿼리 스트링 포함)                               |
| `status`       | 응답 상태 코드                                        |
| `took`         | 처리 소요 시간 (ms)                                   |
| `userId`       | 인증된 사용자 식별자, 비인증 요청은 `-`                        |

```
[a1b2c3d4] c.t.d.g.l.RequestLoggingFilter : GET /api/v1/posts?page=0&size=10 200 15ms userId=1
```

## 요청 ID (MDC)

* 요청마다 UUID 앞 8자리를 생성해 `MDC`의 `requestId` 키에 넣습니다.
* `application.yaml`의 `logging.pattern.correlation`으로 모든 로그 앞에 `[requestId]`가 붙습니다.
* 요청이 끝나면 `MDC.clear()`로 비웁니다. 스레드 풀이 스레드를 재사용하기 때문에 정리하지 않으면 다음 요청에 이전 요청 ID가 남습니다.

> UUID 전체(36자)가 아닌 8자리만 쓰는 이유: 단일 서버 환경에서 요청을 구분하기에 충분하고, 로그 가독성이 좋습니다.

## 로그 레벨 정책

| 레벨      | 사용 상황                                   | 위치                      |
|:--------|:----------------------------------------|:------------------------|
| `INFO`  | 요청 1건의 처리 결과                            | `RequestLoggingFilter`  |
| `WARN`  | 비즈니스 예외, 검증 실패, 표준 MVC 예외 (클라이언트 책임)    | `GlobalExceptionHandler` |
| `ERROR` | 처리되지 않은 예외 (서버 책임, 스택 트레이스 포함)          | `GlobalExceptionHandler` |
| `DEBUG` | 실행 SQL (`org.hibernate.SQL`)            | 개발 환경 전용                |
| `TRACE` | 쿼리 바인딩 파라미터 (`org.hibernate.orm.jdbc.bind`) | 개발 환경 전용            |

클라이언트 입력 문제를 `ERROR`로 남기면 정작 봐야 할 서버 장애가 묻히므로, **예상 가능한 예외는 `WARN`, 예상하지 못한 예외만 `ERROR`** 로 구분합니다.

## 남은 과제

* SQL 로깅은 개발 편의용 설정이므로, 운영 프로파일에서는 분리하여 끈다.
* 로그 파일 저장 및 보관 정책(appender, rolling)은 배포 환경을 정한 뒤 결정한다.
