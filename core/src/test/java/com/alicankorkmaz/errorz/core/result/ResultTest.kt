package com.alicankorkmaz.errorz.core.result

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.failure.UnknownFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {

    @Test
    fun `Success holds data`() {
        val result = Result.Success("hello")
        assertEquals("hello", result.data)
    }

    @Test
    fun `Error holds failure`() {
        val failure = InfrastructureFailure.Timeout
        val result = Result.Error(failure)
        assertEquals(failure, result.failure)
    }

    @Test
    fun `onSuccess is called for Success`() {
        var captured: String? = null
        Result.Success("data").onSuccess { captured = it }
        assertEquals("data", captured)
    }

    @Test
    fun `onSuccess is not called for Error`() {
        var called = false
        Result.Error(InfrastructureFailure.Timeout).onSuccess { called = true }
        assertTrue(!called)
    }

    @Test
    fun `onError is called for Error`() {
        var captured: Failure? = null
        Result.Error(InfrastructureFailure.NoConnection).onError { captured = it }
        assertEquals(InfrastructureFailure.NoConnection, captured)
    }

    @Test
    fun `onError is not called for Success`() {
        var called = false
        Result.Success("data").onError { called = true }
        assertTrue(!called)
    }

    @Test
    fun `map transforms Success data`() {
        val result = Result.Success(5).map { it * 2 }
        assertEquals(10, (result as Result.Success).data)
    }

    @Test
    fun `map passes through Error`() {
        val error = Result.Error(InfrastructureFailure.Timeout)
        val result = error.map { "transformed" }
        assertTrue(result is Result.Error)
    }

    @Test
    fun `flatMap chains Success`() {
        val result = Result.Success(5).flatMap { Result.Success(it.toString()) }
        assertEquals("5", (result as Result.Success).data)
    }

    @Test
    fun `flatMap returns Error from inner`() {
        val result = Result.Success(5).flatMap {
            Result.Error(InfrastructureFailure.Timeout)
        }
        assertTrue(result is Result.Error)
    }

    @Test
    fun `flatMap passes through outer Error`() {
        val error: Result<Int> = Result.Error(InfrastructureFailure.NoConnection)
        val result = error.flatMap { Result.Success(it.toString()) }
        assertEquals(InfrastructureFailure.NoConnection, (result as Result.Error).failure)
    }

    @Test
    fun `mapError transforms failure`() {
        val original = Result.Error(InfrastructureFailure.ServerError(500))
        val mapped = original.mapError { UnknownFailure(RuntimeException("mapped")) }
        assertTrue((mapped as Result.Error).failure is UnknownFailure)
    }

    @Test
    fun `mapError passes through Success`() {
        val result = Result.Success("data").mapError { InfrastructureFailure.Timeout }
        assertEquals("data", (result as Result.Success).data)
    }

    @Test
    fun `onSuccess returns same instance for chaining`() {
        val original = Result.Success("data")
        val returned = original.onSuccess { }
        assertTrue(original === returned)
    }

    @Test
    fun `onError returns same instance for chaining`() {
        val original: Result<String> = Result.Error(InfrastructureFailure.Timeout)
        val returned = original.onError { }
        assertTrue(original === returned)
    }
}
