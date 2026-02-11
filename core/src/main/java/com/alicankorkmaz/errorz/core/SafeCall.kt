package com.alicankorkmaz.errorz.core

import com.alicankorkmaz.errorz.core.api.ApiResponseContract
import com.alicankorkmaz.errorz.core.api.RawApiError
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.result.Result
import kotlinx.coroutines.CancellationException

suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.Error(ExceptionMapperConfig.compositeMapper.map(e))
    }
}

fun <T> handleApiResponse(response: ApiResponseContract<T>): Result<T> {
    return if (response.isSuccessful) {
        val body = response.body
        if (body != null) {
            Result.Success(body)
        } else {
            Result.Error(InfrastructureFailure.ParseError())
        }
    } else {
        val rawApiError = RawApiError(
            httpCode = response.code,
            errorBody = response.errorBody,
        )
        val apiError = rawApiError.errorBody?.let {
            ExceptionMapperConfig.errorBodyParser?.parse(it)
        }
        val failure = when (response.code) {
            401 -> InfrastructureFailure.Unauthorized(apiError)
            else -> InfrastructureFailure.ServerError(response.code, apiError)
        }
        Result.Error(failure)
    }
}
