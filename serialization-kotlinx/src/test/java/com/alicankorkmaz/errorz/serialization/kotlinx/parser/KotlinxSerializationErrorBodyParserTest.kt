package com.alicankorkmaz.errorz.serialization.kotlinx.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KotlinxSerializationErrorBodyParserTest {

    private val parser = KotlinxSerializationErrorBodyParser()

    @Test
    fun `parses valid error body`() {
        val json = """{"message":"Not found","code":"USER_404","errors":["field1","field2"]}"""
        val result = parser.parse(json)!!
        assertEquals("Not found", result.message)
        assertEquals("USER_404", result.code)
        assertEquals(listOf("field1", "field2"), result.errors)
    }

    @Test
    fun `parses minimal error body with defaults`() {
        val json = """{"message":"error"}"""
        val result = parser.parse(json)!!
        assertEquals("error", result.message)
        assertNull(result.code)
        assertEquals(emptyList<String>(), result.errors)
    }

    @Test
    fun `returns null for invalid json`() {
        assertNull(parser.parse("not json"))
    }

    @Test
    fun `returns null for empty string`() {
        assertNull(parser.parse(""))
    }

    @Test
    fun `ignores unknown fields`() {
        val json = """{"message":"ok","unknown_field":"value"}"""
        val result = parser.parse(json)!!
        assertEquals("ok", result.message)
    }

    @Test
    fun `parses empty json object with all defaults`() {
        val json = """{}"""
        val result = parser.parse(json)!!
        assertNull(result.message)
        assertNull(result.code)
        assertEquals(emptyList<String>(), result.errors)
    }
}
