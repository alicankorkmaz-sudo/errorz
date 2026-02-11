package com.alicankorkmaz.errorz.retrofit.mapper

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapper
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import retrofit2.HttpException

class HttpExceptionMapper : ExceptionMapper {

    override fun map(throwable: Throwable): Failure? {
        if (throwable !is HttpException) return null

        val code = throwable.code()
        val errorBody = throwable.response()?.errorBody()?.string()
        val apiError = errorBody?.let {
            ExceptionMapperConfig.errorBodyParser?.parse(it)
        }

        return when (code) {
            401 -> InfrastructureFailure.Unauthorized(apiError)
            else -> InfrastructureFailure.ServerError(code, apiError)
        }
    }
}
