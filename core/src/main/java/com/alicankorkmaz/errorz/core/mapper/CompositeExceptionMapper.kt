package com.alicankorkmaz.errorz.core.mapper

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.UnknownFailure

class CompositeExceptionMapper(
    private val mappers: List<ExceptionMapper>,
) : ExceptionMapper {

    override fun map(throwable: Throwable): Failure {
        for (mapper in mappers) {
            val failure = mapper.map(throwable)
            if (failure != null) return failure
        }
        return UnknownFailure(throwable)
    }
}
