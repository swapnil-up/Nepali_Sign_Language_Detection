package com.example.nsl_mini

import org.junit.Assert.*
import org.junit.Test

class GestureResultFormatterTest {

    @Test
    fun `firstGesture extracts first line`() {
        assertEquals("ka", GestureResultFormatter.firstGesture("ka\nkha\nga"))
    }

    @Test
    fun `firstGesture trims whitespace`() {
        assertEquals("ka", GestureResultFormatter.firstGesture("  ka  \nkha"))
    }

    @Test
    fun `firstGesture returns null for none`() {
        assertNull(GestureResultFormatter.firstGesture("None"))
        assertNull(GestureResultFormatter.firstGesture("none"))
        assertNull(GestureResultFormatter.firstGesture("NONE"))
    }

    @Test
    fun `firstGesture returns null for empty`() {
        assertNull(GestureResultFormatter.firstGesture(""))
    }

    @Test
    fun `compoundCharacters covers all sequences`() {
        val chars = GestureResultFormatter.compoundCharacters
        assertTrue(chars.any { it == "\u0915\u094D\u0937" })
        assertTrue(chars.any { it == "\u0924\u094D\u0930" })
        assertTrue(chars.any { it == "\u091C\u094D\u091E" })
        assertTrue(chars.any { it == "\u0905\u0902" })
        assertTrue(chars.any { it == "\u0905\u0903" })
    }
}
