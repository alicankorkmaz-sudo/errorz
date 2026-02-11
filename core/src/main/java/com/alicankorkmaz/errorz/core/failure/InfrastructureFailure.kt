package com.alicankorkmaz.errorz.core.failure

import com.alicankorkmaz.errorz.core.api.ApiError

sealed class InfrastructureFailure : Failure.Infrastructure {
    data object NoConnection : InfrastructureFailure()
    data object Timeout : InfrastructureFailure()
    data class ServerError(val code: Int, val apiError: ApiError? = null) : InfrastructureFailure()
    data class Unauthorized(val apiError: ApiError? = null) : InfrastructureFailure()
    data class ParseError(val cause: Throwable? = null) : InfrastructureFailure()
}
