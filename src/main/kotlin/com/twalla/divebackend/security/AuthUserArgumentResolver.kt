package com.twalla.divebackend.security

import com.twalla.divebackend.global.error.AuthErrorCode
import com.twalla.divebackend.global.error.BusinessException
import com.twalla.divebackend.user.User
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthUser::class.java) &&
                parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val servletRequest = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: error("HttpServletRequest를 가져올 수 없습니다.")

        return servletRequest.getAttribute(JwtAuthInterceptor.USER_ATTRIBUTE) as? User
            ?: throw BusinessException(AuthErrorCode.UNAUTHENTICATED)
    }

}