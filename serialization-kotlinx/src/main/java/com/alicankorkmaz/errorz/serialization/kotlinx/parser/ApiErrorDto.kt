package com.alicankorkmaz.errorz.serialization.kotlinx.parser

import com.alicankorkmaz.errorz.core.api.ApiError
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val message: String? = null,
    val code: String? = null,
    val errors: List<String> = emptyList(),
) {
    fun toDomain(): ApiError = ApiError(
        message = message,
        code = code,
        errors = errors,
    )
}
