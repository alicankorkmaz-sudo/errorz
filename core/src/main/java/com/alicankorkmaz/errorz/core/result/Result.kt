package com.alicankorkmaz.errorz.core.result

import com.alicankorkmaz.errorz.core.failure.Failure

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val failure: Failure) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (Failure) -> Unit): Result<T> {
    if (this is Result.Error) action(failure)
    return this
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
}

inline fun <T> Result<T>.mapError(transform: (Failure) -> Failure): Result<T> = when (this) {
    is Result.Success -> this
    is Result.Error -> Result.Error(transform(failure))
}
