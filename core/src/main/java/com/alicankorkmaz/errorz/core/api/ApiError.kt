package com.alicankorkmaz.errorz.core.api

data class ApiError(
    val message: String?,
    val code: String?,
    val errors: List<String> = emptyList(),
)
