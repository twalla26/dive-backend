# Database Schema

## 1. USER (사용자)

| 컬럼명          | 데이터 타입       |   키    | 설명         |
|:-------------|:-------------|:------:|:-----------|
| `id`         | bigint       | **PK** | 사용자 고유 식별자 |
| `email`      | varchar(255) | **UK** | 이메일 (고유값)  |
| `password`   | varchar(255) |        | 비밀번호 (암호화) |
| `nickname`   | varchar(50)  |        | 닉네임        |
| `created_at` | timestamp    |        | 계정 생성 일시   |

<br>

## 2. REFRESH_TOKEN (리프레시 토큰)

| 컬럼명             | 데이터 타입       |   키    | 설명                  |
|:----------------|:-------------|:------:|:--------------------|
| `id`            | bigint       | **PK** | 토큰 고유 식별자           |
| `refresh_token` | varchar(512) |        | 리프레시 토큰 값           |
| `created_at`    | timestamp    |        | 토큰 발급 일시            |
| `expires_at`    | timestamp    |        | 토큰 만료 일시            |
| `user_id`       | bigint       | **FK** | 소유자 식별자 (`USER.id`) |

<br>

## 3. POST (게시글)

| 컬럼명             | 데이터 타입    |   키    | 설명                      |
|:----------------|:----------|:------:|:------------------------|
| `id`            | bigint    | **PK** | 게시글 고유 식별자              |
| `content`       | text      |        | 게시글 본문 내용               |
| `view_count`    | bigint    |        | 조회수                     |
| `comment_count` | int       |        | 댓글수 (역정규화)              |
| `like_count`    | int       |        | 좋아요수 (역정규화)             |
| `created_at`    | timestamp |        | 게시글 작성 일시               |
| `updated_at`    | timestamp |        | 게시글 수정 일시               |
| `deleted_at`    | timestamp |        | 게시글 삭제 일시 (Soft Delete) |
| `user_id`       | bigint    | **FK** | 작성자 식별자 (`USER.id`)     |

<br>

## 4. COMMENT (댓글)

| 컬럼명          | 데이터 타입    |   키    | 설명                     |
|:-------------|:----------|:------:|:-----------------------|
| `id`         | bigint    | **PK** | 댓글 고유 식별자              |
| `content`    | text      |        | 댓글 본문 내용               |
| `like_count` | int       |        | 좋아요수 (역정규화)            |
| `created_at` | timestamp |        | 댓글 작성 일시               |
| `deleted_at` | timestamp |        | 댓글 삭제 일시 (Soft Delete) |
| `post_id`    | bigint    | **FK** | 소속 게시글 식별자 (`POST.id`) |
| `user_id`    | bigint    | **FK** | 작성자 식별자 (`USER.id`)    |

<br>

## 5. POST_LIKE (게시글 좋아요)

| 컬럼명          | 데이터 타입    |     키      | 설명                     |
|:-------------|:----------|:----------:|:-----------------------|
| `id`         | bigint    |   **PK**   | 식별자                    |
| `user_id`    | bigint    | **FK, UK** | 누른 사용자 식별자 (`USER.id`) |
| `post_id`    | bigint    | **FK, UK** | 대상 게시글 식별자 (`POST.id`) |
| `created_at` | timestamp |            | 좋아요 누른 일시              |

<br>

## 6. COMMENT_LIKE (댓글 좋아요)

| 컬럼명          | 데이터 타입    |     키      | 설명                       |
|:-------------|:----------|:----------:|:-------------------------|
| `id`         | bigint    |   **PK**   | 식별자                      |
| `user_id`    | bigint    | **FK, UK** | 누른 사용자 식별자 (`USER.id`)   |
| `comment_id` | bigint    | **FK, UK** | 대상 댓글 식별자 (`COMMENT.id`) |
| `created_at` | timestamp |            | 좋아요 누른 일시                |
