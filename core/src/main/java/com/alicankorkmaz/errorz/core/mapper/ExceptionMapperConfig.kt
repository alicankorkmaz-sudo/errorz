package com.alicankorkmaz.errorz.core.mapper

import com.alicankorkmaz.errorz.core.api.ErrorBodyParser

object ExceptionMapperConfig {

    lateinit var compositeMapper: CompositeExceptionMapper
        private set

    var errorBodyParser: ErrorBodyParser? = null
        private set

    fun configure(
        mappers: List<ExceptionMapper>,
        errorBodyParser: ErrorBodyParser? = null,
    ) {
        this.compositeMapper = CompositeExceptionMapper(mappers)
        this.errorBodyParser = errorBodyParser
    }
}
