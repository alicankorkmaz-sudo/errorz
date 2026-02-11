package com.alicankorkmaz.errorz.core

import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.failure.UnknownFailure
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.mapper.NetworkExceptionMapper
import com.alicankorkmaz.errorz.core.result.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SafeCallTest {

    @Before
    fun setup() {
        ExceptionMapperConfig.configure(listOf(NetworkExceptionMapper()))
    }

    @Test
    fun `returns Success on successful block`() = runTest {
        val result = safeCall { "hello" }
        assertEquals("hello", (result as Result.Success).data)
    }

    @Test
    fun `maps IOException to NoConnection`() = runTest {
        val result = safeCall { throw IOException() }
        assertEquals(InfrastructureFailure.NoConnection, (result as Result.Error).failure)
    }

    @Test
    fun `maps unknown exception to UnknownFailure`() = runTest {
        val ex = IllegalStateException("boom")
        val result = safeCall { throw ex }
        val failure = (result as Result.Error).failure as UnknownFailure
        assertEquals(ex, failure.throwable)
    }

    @Test
    fun `rethrows CancellationException`() = runTest {
        try {
            safeCall { throw CancellationException("cancelled") }
            fail("Expected CancellationException")
        } catch (e: CancellationException) {
            assertEquals("cancelled", e.message)
        }
    }
}
