package com.alicankorkmaz.errorz.retrofit

import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.failure.UnknownFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.mapper.NetworkExceptionMapper
import com.alicankorkmaz.errorz.core.result.Result
import com.alicankorkmaz.errorz.retrofit.mapper.HttpExceptionMapper
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class SafeApiCallTest {

    @Before
    fun setup() {
        ExceptionMapperConfig.configure(
            listOf(NetworkExceptionMapper(), HttpExceptionMapper())
        )
    }

    @Test
    fun `returns Success for successful response`() = runTest {
        val result = safeApiCall { Response.success("hello") }
        assertEquals("hello", (result as Result.Success).data)
    }

    @Test
    fun `returns ServerError for error response`() = runTest {
        val result = safeApiCall { Response.error<String>(500, "".toResponseBody()) }
        val failure = (result as Result.Error).failure as InfrastructureFailure.ServerError
        assertEquals(500, failure.code)
    }

    @Test
    fun `returns Unauthorized for 401 response`() = runTest {
        val result = safeApiCall { Response.error<String>(401, "".toResponseBody()) }
        assertTrue((result as Result.Error).failure is InfrastructureFailure.Unauthorized)
    }

    @Test
    fun `maps IOException to NoConnection`() = runTest {
        val result = safeApiCall<String> { throw IOException() }
        assertEquals(InfrastructureFailure.NoConnection, (result as Result.Error).failure)
    }

    @Test
    fun `maps unknown exception to UnknownFailure`() = runTest {
        val ex = IllegalStateException("boom")
        val result = safeApiCall<String> { throw ex }
        val failure = (result as Result.Error).failure as UnknownFailure
        assertEquals(ex, failure.throwable)
    }

    @Test
    fun `safeApiCallNullable returns Success with null body`() = runTest {
        val result = safeApiCallNullable { Response.success<String?>(null) }
        assertTrue(result is Result.Success)
        assertEquals(null, (result as Result.Success).data)
    }

    @Test
    fun `safeApiCallNullable returns Success with body`() = runTest {
        val result = safeApiCallNullable { Response.success<String?>("data") }
        assertEquals("data", (result as Result.Success).data)
    }

    @Test
    fun `safeApiCallNullable returns error for failed response`() = runTest {
        val result = safeApiCallNullable { Response.error<String?>(500, "".toResponseBody()) }
        val failure = (result as Result.Error).failure as InfrastructureFailure.ServerError
        assertEquals(500, failure.code)
    }
}
