package com.alicankorkmaz.errorz.serialization.kotlinx.mapper

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapper
import kotlinx.serialization.SerializationException

class KotlinxSerializationExceptionMapper : ExceptionMapper {

    override fun map(throwable: Throwable): Failure? {
        if (throwable is SerializationException) {
            return InfrastructureFailure.ParseError(throwable)
        }
        return null
    }
}
