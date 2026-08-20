## 요약

| 메서드  | 엔드포인트           | 설명     | 인증 |
|------|-----------------|--------|----|
| POST | `/auth/signup`  | 회원가입   | ❌  |
| POST | `/auth/login`   | 로그인    | ❌  |
| POST | `/auth/refresh` | 토큰 재발급 | ❌  |

## 회원가입

### 요청

```
POST /api/v1/auth/signup
Content-Type: application/json

{
    "email": "eee@naver.com",
    "password": "abcd1234!!",
    "nickname": "fishAndChips"
}
```

### 응답

```
201 CREATED

{
    "email": "eee@naver.com",
    "nickname": "fishAndChips"
}
```

## 로그인

### 요청

```
POST /api/v1/auth/login
Content-Type: application/json

{
    "email": "aaa@naver.com",
    "password": "abcd1234!!"
}
```

### 응답

```
200 OK

{
    "accessToken": "eyJhbGc",
    "refreshToken": "eyJhbGc"
}
```

## 토큰 재발급

### 요청

```
POST /api/v1/auth/refresh
Content-Type: application/json

{
    "refreshToken": "eyJhbGc"
}
```

### 응답

```
200 OK

{
    "accessToken": "eyJhbGc",
    "refreshToken": "eyJhbGc"
}
```


