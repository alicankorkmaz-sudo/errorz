package com.alicankorkmaz.errorz.core.mapper

import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkExceptionMapperTest {

    private val mapper = NetworkExceptionMapper()

    @Test
    fun `maps SocketTimeoutException to Timeout`() {
        assertEquals(InfrastructureFailure.Timeout, mapper.map(SocketTimeoutException()))
    }

    @Test
    fun `maps UnknownHostException to NoConnection`() {
        assertEquals(InfrastructureFailure.NoConnection, mapper.map(UnknownHostException()))
    }

    @Test
    fun `maps IOException to NoConnection`() {
        assertEquals(InfrastructureFailure.NoConnection, mapper.map(IOException()))
    }

    @Test
    fun `returns null for unrelated exceptions`() {
        assertNull(mapper.map(RuntimeException()))
        assertNull(mapper.map(IllegalArgumentException()))
    }
}
