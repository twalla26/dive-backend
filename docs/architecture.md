# Architecture

Kotlin + Spring Boot 기반의 레이어드 아키텍처입니다.
패키지는 기술 레이어가 아니라 도메인(`auth`, `user`, `post`, `comment`) 단위로 나눕니다.

<br>

## 1. 구조도

![architecture.png](image/architecture.png)

```mermaid
graph TD
    Client["Client"]
    Interceptor["JwtAuthInterceptor<br/>인증"]
    Controller["Controller<br/>요청/응답, 검증"]
    Service["Service<br/>@Transactional, 비즈니스 로직"]
    Repository["Repository<br/>Spring Data JPA"]
    DB[("MySQL")]

    Client --> Interceptor --> Controller --> Service --> Repository --> DB
```

| 레이어          | 책임                                    |
|:-------------|:--------------------------------------|
| `Controller` | HTTP 매핑, `@Valid` 검증, 상태 코드 결정. 로직 없음 |
| `Service`    | 트랜잭션 경계, 권한 확인, 도메인 규칙, DTO 변환        |
| `Repository` | 쿼리. 파생 쿼리 + `@Modifying @Query` 벌크 연산 |

의존 방향은 단방향입니다. Repository는 Service를 모르고, 엔티티는 DTO를 모릅니다.

<br>

## 2. 요청 흐름도

`POST /api/v1/posts` (게시글 작성)를 대표 예시로 든 흐름입니다.

![sequence.png](image/sequence.png)

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant IC as JwtAuthInterceptor
    participant CT as PostController
    participant SV as PostService
    participant RP as PostRepository
    participant DB as MySQL

    C->>IC: POST /api/v1/posts<br/>Authorization: Bearer eyJ...
    IC->>IC: 토큰 검증 → User 조회
    Note over IC: 실패 시 401

    IC->>CT: @AuthUser User 주입<br/>@Valid 요청 본문 검증
    CT->>SV: createPost(user, request)

    rect rgb(240, 244, 250)
        Note over SV,DB: @Transactional
        SV->>RP: save(post)
        RP->>DB: INSERT INTO post
        DB-->>RP: generated id
    end

    SV-->>CT: PostDetailResponse
    CT-->>C: 201 Created
```
