package com.alicankorkmaz.errorz.retrofit.mapper

import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.mapper.NetworkExceptionMapper
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class HttpExceptionMapperTest {

    private val mapper = HttpExceptionMapper()

    @Before
    fun setup() {
        ExceptionMapperConfig.configure(listOf(NetworkExceptionMapper()))
    }

    @Test
    fun `maps 401 HttpException to Unauthorized`() {
        val response = Response.error<String>(401, "".toResponseBody())
        val result = mapper.map(HttpException(response))
        assertTrue(result is InfrastructureFailure.Unauthorized)
    }

    @Test
    fun `maps 500 HttpException to ServerError`() {
        val response = Response.error<String>(500, "".toResponseBody())
        val result = mapper.map(HttpException(response))
        val failure = result as InfrastructureFailure.ServerError
        assertEquals(500, failure.code)
    }

    @Test
    fun `maps 404 HttpException to ServerError with code`() {
        val response = Response.error<String>(404, "".toResponseBody())
        val result = mapper.map(HttpException(response))
        val failure = result as InfrastructureFailure.ServerError
        assertEquals(404, failure.code)
    }

    @Test
    fun `returns null for non-HttpException`() {
        assertNull(mapper.map(RuntimeException()))
        assertNull(mapper.map(IllegalArgumentException()))
    }
}
