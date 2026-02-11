package com.alicankorkmaz.errorz.core.mapper

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkExceptionMapper : ExceptionMapper {

    override fun map(throwable: Throwable): Failure? = when (throwable) {
        is SocketTimeoutException -> InfrastructureFailure.Timeout
        is UnknownHostException -> InfrastructureFailure.NoConnection
        is IOException -> InfrastructureFailure.NoConnection
        else -> null
    }
}
