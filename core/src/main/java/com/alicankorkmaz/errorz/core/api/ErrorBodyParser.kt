package com.alicankorkmaz.errorz.core.api

interface ErrorBodyParser {
    fun parse(errorBody: String): ApiError?
}
