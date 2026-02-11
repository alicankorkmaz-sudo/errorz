package com.alicankorkmaz.errorz.serialization.kotlinx.parser

import com.alicankorkmaz.errorz.core.api.ApiError
import com.alicankorkmaz.errorz.core.api.ErrorBodyParser
import kotlinx.serialization.json.Json

class KotlinxSerializationErrorBodyParser(
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ErrorBodyParser {

    override fun parse(errorBody: String): ApiError? {
        return try {
            json.decodeFromString<ApiErrorDto>(errorBody).toDomain()
        } catch (_: Exception) {
            null
        }
    }
}
