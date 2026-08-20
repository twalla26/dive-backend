## 요약

| 메서드    | 엔드포인트                      | 설명       | 인증 |
|--------|----------------------------|----------|----|
| GET    | `/posts/{postId}/comments` | 댓글 목록 조회 | ✅  |
| POST   | `/posts/{postId}/comments` | 댓글 작성    | ✅  |
| DELETE | `/comments/{commentId}`    | 댓글 삭제    | ✅  |

## 댓글 목록 조회

### 요청

```
GET /api/v1/posts/{{postId}}/comments
Authorization: Bearer {{token}}
```

### 응답

```
200 OK

{
  "comments": [
    {
      "id": 1,
      "content": "첫 댓글",
      "likeCount": 0,
      "createdAt": "2026-08-20T03:10:22Z",
      "userId": 2,
      "nickname": "moomu"
    },
    {
      "id": 2,
      "content": "두 번째 댓글",
      "likeCount": 3,
      "createdAt": "2026-08-20T04:02:11Z",
      "userId": 1,
      "nickname": "twalla"
    }
  ]
}
```

### 에러

| 상태  | 상황               |
|-----|------------------|
| 404 | 존재하지 않거나 삭제된 게시글 |

## 댓글 작성

### 요청

```
POST /api/v1/posts/{{postId}}/comments
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "content": "첫 댓글"
}
```

### 응답

```
201 CREATED

{
  "id": 2,
  "content": "첫 댓글",
  "likeount": 0,
  "postId": 1,
  "author": {
    "userId": 1,
    "nickname": "twalla"
  },
  "createdAt": "2026-08-20T05:30:10.970580Z"
}
```

### 에러

| 상태  | 상황                                |
|-----|-----------------------------------|
| 400 | `content`가 공백이거나 2000자 초과         |
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 |
| 404 | 존재하지 않거나 삭제된 게시글                  |

## 댓글 삭제

`docs/delete-policy.md`에 따라 hard delete로 처리한다. 복구 수단이 없어 응답 본문에 담을 정보가 없으므로 `204 NO CONTENT`를 반환한다.

### 요청

```
DELETE /api/v1/comments/{{commentId}}
Authorization: Bearer {{token}}
```

### 응답

```
204 NO CONTENT
```

### 에러

| 상태  | 상황                                |
|-----|-----------------------------------|
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 |
| 403 | 본인이 작성한 댓글이 아님                    |
| 404 | 존재하지 않는 게시글 · 댓글                  |
