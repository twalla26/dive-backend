package com.twalla.divebackend.global.error

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant


data class ApiErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val code: String,
    val message: String,
)

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // 비지니스 예외 처리
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiErrorResponse> {
        log.warn("비즈니스 예외: {} - {}", e.errorCode.code, e.message)
        return toResponse(e.errorCode)
    }

    // @Valid DTO 검증 실패 예외
    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val message = e.bindingResult.allErrors.firstOrNull()?.defaultMessage
            ?: CommonErrorCode.INVALID_INPUT.message

        log.warn("검증 실패: {}", e.bindingResult.allErrors)

        return ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                code = CommonErrorCode.INVALID_INPUT.code,
                message = message,
            )
        )
    }

    // 나머지 표준 MVC 예외(400/404/405/415…)를 공통 포맷으로 변환
    override fun handleExceptionInternal(
        e: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorCode = when (statusCode.value()) {
            404 -> CommonErrorCode.RESOURCE_NOT_FOUND
            405 -> CommonErrorCode.METHOD_NOT_ALLOWED
            415 -> CommonErrorCode.UNSUPPORTED_MEDIA_TYPE
            else -> if (statusCode.is4xxClientError) CommonErrorCode.INVALID_INPUT
            else CommonErrorCode.INTERNAL_SERVER_ERROR
        }

        log.warn("MVC 예외: {}", errorCode.code)

        return ResponseEntity.status(statusCode).body(
            ApiErrorResponse(
                status = statusCode.value(),
                code = errorCode.code,
                message = errorCode.message,
            )
        )
    }

    // 나머지 모든 예외
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiErrorResponse> {
        log.error("처리되지 않은 에러 발생", e)
        return toResponse(CommonErrorCode.INTERNAL_SERVER_ERROR)
    }

    private fun toResponse(errorCode: ErrorCode): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(errorCode.status).body(
            ApiErrorResponse(
                status = errorCode.status.value(),
                code = errorCode.code,
                message = errorCode.message,
            )
        )
}