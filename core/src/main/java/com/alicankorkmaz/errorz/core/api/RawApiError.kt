package com.alicankorkmaz.errorz.core.api

data class RawApiError(
    val httpCode: Int,
    val errorBody: String?,
)
