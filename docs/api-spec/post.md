## 요약

| 메서드    | 엔드포인트             | 설명        | 인증 |
|--------|-------------------|-----------|----|
| GET    | `/posts`          | 게시글 목록 조회 | ✅  |
| GET    | `/posts/{postId}` | 게시글 상세 조회 | ✅  |
| POST   | `/posts`          | 게시글 작성    | ✅  |
| PATCH  | `/posts/{postId}` | 게시글 수정    | ✅  |
| DELETE | `/posts/{postId}` | 게시글 삭제    | ✅  |

## 게시글 목록 조회

삭제된 게시글은 제외하며, 작성일 내림차순(최신순)으로 정렬한다.
목록에서는 본문을 100자까지만 반환한다.

### 요청

```
GET /api/v1/posts
Authorization: Bearer {{token}}
```

### 응답

```
200 OK

{
  "posts": [
    {
      "id": 5,
      "content": "test5",
      "commentCount": 0,
      "likeCount": 0,
      "createdAt": "2026-08-04T12:59:07Z",
      "author": {
        "userId": 1,
        "nickname": "twalla"
      }
    },
    {
      "id": 2,
      "content": "test2~...",
      "commentCount": 3,
      "likeCount": 0,
      "createdAt": "2026-08-02T04:31:47Z",
      "author": {
        "userId": 2,
        "nickname": "moomu"
      }
    }
  ]
}
```

### 에러

| 상태  | 상황                                |
|-----|-----------------------------------|
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 |

## 게시글 상세 조회

조회할 때마다 `viewCount`가 1 증가하며, 증가된 값이 응답에 반영된다.
목록과 달리 본문 전체를 반환한다.

### 요청

```
GET /api/v1/posts/{{postId}}
Authorization: Bearer {{token}}
```

### 응답

```
200 OK

{
  "id": 1,
  "content": "updated 5",
  "viewCount": 8,
  "commentCount": 0,
  "likeCount": 0,
  "createdAt": "2026-08-02T02:57:58Z",
  "updatedAt": "2026-08-04T12:59:14Z",
  "author": {
    "userId": 1,
    "nickname": "twalla"
  }
}
```

### 에러

| 상태  | 상황                                |
|-----|-----------------------------------|
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 |
| 404 | 존재하지 않거나 삭제된 게시글                  |

## 게시글 작성

작성된 게시글을 상세 조회와 동일한 형태로 반환한다.

### 요청

```
POST /api/v1/posts
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "content": "test5"
}
```

### 응답

```
201 CREATED

{
  "id": 5,
  "content": "test5",
  "viewCount": 0,
  "commentCount": 0,
  "likeCount": 0,
  "createdAt": "2026-08-04T12:59:07Z",
  "updatedAt": "2026-08-04T12:59:07Z",
  "author": {
    "userId": 1,
    "nickname": "twalla"
  }
}
```

### 에러

| 상태  | 상황                                        |
|-----|-------------------------------------------|
| 400 | `content`가 공백이거나 5000자 초과                 |
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 / 유저 없음 |

## 게시글 수정

본인이 작성한 게시글만 수정할 수 있다.
`content`를 생략하면 아무것도 변경하지 않고 현재 상태를 반환한다.

### 요청

```
PATCH /api/v1/posts/{{postId}}
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "content": "updated 5"
}
```

### 응답

```
200 OK

{
  "id": 1,
  "content": "updated 5",
  "viewCount": 7,
  "commentCount": 0,
  "likeCount": 0,
  "createdAt": "2026-08-02T02:57:58Z",
  "updatedAt": "2026-08-04T12:59:14Z",
  "author": {
    "userId": 1,
    "nickname": "twalla"
  }
}
```

### 에러

| 상태  | 상황                                |
|-----|-----------------------------------|
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 |
| 403 | 본인이 작성한 게시글이 아님                   |
| 404 | 존재하지 않거나 삭제된 게시글                  |

## 게시글 삭제

`docs/delete-policy.md`에 따라 soft delete로 처리한다.
`deletedAt`으로부터 30일간 복구할 수 있으며, 그 기한을 `restorableUntil`로 함께 반환한다.

### 요청

```
DELETE /api/v1/posts/{{postId}}
Authorization: Bearer {{token}}
```

### 응답

```
200 OK

{
  "id": 1,
  "deletedAt": "2026-08-20T03:56:15.057327Z",
  "restorableUntil": "2026-09-19T03:56:15.057327Z"
}
```

### 에러

| 상태  | 상황                                |
|-----|-----------------------------------|
| 401 | Authorization 헤더 없음 / 토큰 만료 · 위변조 |
| 403 | 본인이 작성한 게시글이 아님                   |
| 404 | 존재하지 않거나 이미 삭제된 게시글               |

## 게시글 좋아요 추가
