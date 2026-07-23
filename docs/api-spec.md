## 전체 API 초안 설계

### 인증 (Auth)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| POST | `/auth/register` | 회원가입 | ❌ |
| POST | `/auth/login` | 로그인 | ❌ |
| POST | `/auth/logout` | 로그아웃 | ✅ |

### 사용자 (User)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| GET | `/users/me` | 내 정보 조회 | ✅ |
| GET | `/users/me/posts` | 내 게시글 조회 | ✅ |
| DELETE | `/users/me` | 회원 탈퇴 | ✅ |

### 게시글 (Post)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| GET | `/posts` | 게시글 목록 조회 | ❌ |
| GET | `/posts/{id}` | 게시글 상세 조회 | ❌ |
| POST | `/posts` | 게시글 작성 | ✅ |
| PUT | `/posts/{id}` | 게시글 수정 | ✅ |
| DELETE | `/posts/{id}` | 게시글 삭제 | ✅ |

### 댓글 (Comment)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| GET | `/posts/{id}/comments` | 댓글 목록 조회 | ❌ |
| POST | `/posts/{id}/comments` | 댓글 작성 | ✅ |
| DELETE | `/posts/{id}/comments/{commentId}` | 댓글 삭제 | ✅ |

### 좋아요 (Like)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| POST | `/posts/{id}/likes` | 좋아요 추가 | ✅ |
| DELETE | `/posts/{id}/likes` | 좋아요 취소 | ✅ |

### 팔로우 (Follow)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| POST | `/users/{id}/follow` | 팔로우 | ✅ |
| DELETE | `/users/{id}/follow` | 언팔로우 | ✅ |
| GET | `/users/me/feed` | 팔로잉 피드 조회 | ✅ |

### 검색 (Search)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| GET | `/search/posts?q={keyword}` | 게시글 검색 | ❌ |
| GET | `/search/users?q={keyword}` | 유저 검색 | ❌ |