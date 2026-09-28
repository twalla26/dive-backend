package com.twalla.divebackend.global.error

import org.springframework.http.HttpStatus

interface ErrorCode {
    val status: HttpStatus
    val code: String
    val message: String
}

enum class CommonErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "api.common.invalidInput", "유효하지 않은 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "api.common.resourceNotFound", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "api.common.methodNotAllowed", "지원하지 않는 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "api.common.unsupportedMediaType", "지원하지 않는 미디어 타입입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "api.common.internalServerError", "서버 내부 오류가 발생했습니다.")
}

enum class AuthErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "api.auth.unauthenticated", "로그인이 필요한 요청입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "api.auth.duplicateEmail", "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "api.auth.invalidCredentials", "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "api.auth.invalidToken", "유효하지 않은 토큰입니다.")
}

enum class UserErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "api.user.userNotFound", "존재하지 않는 유저입니다."),
}

enum class PostErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "api.post.postNotFound", "존재하지 않는 게시글입니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "api.post.postAccessDenied", "해당 게시글에 대한 권한이 없습니다."),
    CANNOT_LIKE_OWN_POST(HttpStatus.BAD_REQUEST, "api.post.cannotLikeOwnPost", "자신의 글에는 좋아요를 누를 수 없습니다.")
}

enum class CommentErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "api.comment.commentNotFound", "존재하지 않는 댓글입니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "api.comment.commentAccessDenied", "해당 댓글에 대한 권한이 없습니다.")
}