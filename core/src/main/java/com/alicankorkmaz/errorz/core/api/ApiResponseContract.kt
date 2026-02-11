package com.alicankorkmaz.errorz.core.api

interface ApiResponseContract<T> {
    val isSuccessful: Boolean
    val code: Int
    val body: T?
    val errorBody: String?
}
