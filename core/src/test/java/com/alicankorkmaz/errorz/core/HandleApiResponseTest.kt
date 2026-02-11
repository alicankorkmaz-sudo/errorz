package com.alicankorkmaz.errorz.core

import com.alicankorkmaz.errorz.core.api.ApiError
import com.alicankorkmaz.errorz.core.api.ApiResponseContract
import com.alicankorkmaz.errorz.core.api.ErrorBodyParser
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.mapper.NetworkExceptionMapper
import com.alicankorkmaz.errorz.core.result.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HandleApiResponseTest {

    @Before
    fun setup() {
        ExceptionMapperConfig.configure(
            mappers = listOf(NetworkExceptionMapper()),
            errorBodyParser = object : ErrorBodyParser {
                override fun parse(errorBody: String): ApiError? {
                    if (errorBody.contains("parsed")) {
                        return ApiError(message = "parsed", code = "ERR", errors = emptyList())
                    }
                    return null
                }
            }
        )
    }

    @Test
    fun `successful response with body returns Success`() {
        val response = fakeResponse(isSuccessful = true, code = 200, body = "data", errorBody = null)
        val result = handleApiResponse(response)
        assertEquals("data", (result as Result.Success).data)
    }

    @Test
    fun `successful response with null body returns ParseError`() {
        val response = fakeResponse(isSuccessful = true, code = 200, body = null, errorBody = null)
        val result = handleApiResponse(response)
        assertTrue((result as Result.Error).failure is InfrastructureFailure.ParseError)
    }

    @Test
    fun `401 returns Unauthorized`() {
        val response = fakeResponse(isSuccessful = false, code = 401, body = null, errorBody = null)
        val result = handleApiResponse(response)
        assertTrue((result as Result.Error).failure is InfrastructureFailure.Unauthorized)
    }

    @Test
    fun `500 returns ServerError with code`() {
        val response = fakeResponse(isSuccessful = false, code = 500, body = null, errorBody = null)
        val result = handleApiResponse(response)
        val failure = (result as Result.Error).failure as InfrastructureFailure.ServerError
        assertEquals(500, failure.code)
    }

    @Test
    fun `error body is parsed into ApiError`() {
        val response = fakeResponse(isSuccessful = false, code = 422, body = null, errorBody = "parsed")
        val result = handleApiResponse(response)
        val failure = (result as Result.Error).failure as InfrastructureFailure.ServerError
        assertEquals("parsed", failure.apiError?.message)
    }

    @Test
    fun `unparseable error body results in null apiError`() {
        val response = fakeResponse(isSuccessful = false, code = 500, body = null, errorBody = "garbage")
        val result = handleApiResponse(response)
        val failure = (result as Result.Error).failure as InfrastructureFailure.ServerError
        assertEquals(null, failure.apiError)
    }

    private fun <T> fakeResponse(
        isSuccessful: Boolean,
        code: Int,
        body: T?,
        errorBody: String?,
    ): ApiResponseContract<T> = object : ApiResponseContract<T> {
        override val isSuccessful = isSuccessful
        override val code = code
        override val body = body
        override val errorBody = errorBody
    }
}
