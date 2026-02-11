package com.alicankorkmaz.errorz.core.mapper

import com.alicankorkmaz.errorz.core.failure.Failure

interface ExceptionMapper {
    fun map(throwable: Throwable): Failure?
}
