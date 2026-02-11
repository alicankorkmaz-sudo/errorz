package com.alicankorkmaz.errorz

import android.app.Application
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.mapper.NetworkExceptionMapper
import com.alicankorkmaz.errorz.retrofit.mapper.HttpExceptionMapper
import com.alicankorkmaz.errorz.serialization.kotlinx.mapper.KotlinxSerializationExceptionMapper
import com.alicankorkmaz.errorz.serialization.kotlinx.parser.KotlinxSerializationErrorBodyParser

class ErrorzApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ExceptionMapperConfig.configure(
            mappers = listOf(
                NetworkExceptionMapper(),
                HttpExceptionMapper(),
                KotlinxSerializationExceptionMapper(),
            ),
            errorBodyParser = KotlinxSerializationErrorBodyParser(),
        )
    }
}
