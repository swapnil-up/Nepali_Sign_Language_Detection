package com.example.nsl_mini

import org.junit.Assert.*
import org.junit.Test

class QuizStorageTest {

    private val storage: QuizStorage = InMemoryQuizStorage()

    @Test
    fun `save and load quiz`() {
        val quiz = Quiz(id = "1", imageUrl = "img.jpg", options = listOf("a", "b", "c", "d"), correctAnswer = "a")
        storage.saveQuiz(quiz)

        val loaded = storage.loadQuiz("1")
        assertEquals(quiz, loaded)
    }

    @Test
    fun `save updates existing quiz`() {
        storage.saveQuiz(Quiz(id = "1", imageUrl = "old.jpg", correctAnswer = "b"))
        storage.saveQuiz(Quiz(id = "1", imageUrl = "new.jpg", correctAnswer = "a"))

        val loaded = storage.loadQuiz("1")
        assertEquals("new.jpg", loaded?.imageUrl)
        assertEquals("a", loaded?.correctAnswer)
    }

    @Test
    fun `delete quiz`() {
        storage.saveQuiz(Quiz(id = "1"))
        storage.deleteQuiz("1")

        assertNull(storage.loadQuiz("1"))
        assertTrue(storage.loadAllQuizzes().isEmpty())
    }

    @Test
    fun `loadAllQuizzes returns all`() {
        storage.saveQuiz(Quiz(id = "1"))
        storage.saveQuiz(Quiz(id = "2"))

        assertEquals(2, storage.loadAllQuizzes().size)
    }

    @Test
    fun `load nonexistent quiz returns null`() {
        assertNull(storage.loadQuiz("nonexistent"))
    }

    @Test
    fun `empty storage returns empty list`() {
        assertTrue(storage.loadAllQuizzes().isEmpty())
    }
}
