package com.alicankorkmaz.errorz.core.mapper

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.failure.UnknownFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompositeExceptionMapperTest {

    @Test
    fun `returns first matching mapper result`() {
        val mapper = CompositeExceptionMapper(
            listOf(
                object : ExceptionMapper {
                    override fun map(throwable: Throwable): Failure? = null
                },
                object : ExceptionMapper {
                    override fun map(throwable: Throwable): Failure = InfrastructureFailure.Timeout
                },
            )
        )
        assertEquals(InfrastructureFailure.Timeout, mapper.map(RuntimeException()))
    }

    @Test
    fun `falls back to UnknownFailure when no mapper matches`() {
        val exception = RuntimeException("test")
        val mapper = CompositeExceptionMapper(
            listOf(
                object : ExceptionMapper {
                    override fun map(throwable: Throwable): Failure? = null
                },
            )
        )
        val result = mapper.map(exception)
        assertTrue(result is UnknownFailure)
        assertEquals(exception, (result as UnknownFailure).throwable)
    }

    @Test
    fun `empty mapper list falls back to UnknownFailure`() {
        val mapper = CompositeExceptionMapper(emptyList())
        val result = mapper.map(RuntimeException())
        assertTrue(result is UnknownFailure)
    }

    @Test
    fun `stops at first match and does not call subsequent mappers`() {
        var secondCalled = false
        val mapper = CompositeExceptionMapper(
            listOf(
                object : ExceptionMapper {
                    override fun map(throwable: Throwable): Failure = InfrastructureFailure.NoConnection
                },
                object : ExceptionMapper {
                    override fun map(throwable: Throwable): Failure? {
                        secondCalled = true
                        return InfrastructureFailure.Timeout
                    }
                },
            )
        )
        mapper.map(RuntimeException())
        assertTrue(!secondCalled)
    }
}
