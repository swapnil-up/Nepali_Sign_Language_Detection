package com.example.nsl_mini

import org.junit.Assert.*
import org.junit.Test

class FakeGestureRecognizerTest {

    @Test
    fun `fake recognizer records setup call`() {
        val fake = FakeGestureRecognizer()
        fake.setup("test_model.task")
        assertTrue(fake.wasSetupCalled)
    }

    @Test
    fun `fake recognizer returns configured result`() {
        val landmarks = listOf(
            HandLandmark(0.1f, 0.2f, 0.3f),
            HandLandmark(0.4f, 0.5f, 0.6f),
        )
        val fake = FakeGestureRecognizer(result = "test", landmarks = landmarks)
        val (result, landmarkList) = fake.buildResult()
        assertEquals("test", result)
        assertEquals(2, landmarkList.size)
        assertEquals(0.1f, landmarkList[0].x)
    }
}
