package com.alicankorkmaz.errorz.serialization.kotlinx.mapper

import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinxSerializationExceptionMapperTest {

    private val mapper = KotlinxSerializationExceptionMapper()

    @Test
    fun `maps SerializationException to ParseError`() {
        val exception = SerializationException("bad json")
        val result = mapper.map(exception)
        assertTrue(result is InfrastructureFailure.ParseError)
        assertEquals(exception, (result as InfrastructureFailure.ParseError).cause)
    }

    @Test
    fun `returns null for non-SerializationException`() {
        assertNull(mapper.map(RuntimeException()))
        assertNull(mapper.map(IllegalArgumentException()))
    }
}
