package com.example.nsl_mini

class InMemoryQuizStorage : QuizStorage {
    private val quizzes = mutableListOf<Quiz>()

    override fun saveQuiz(quiz: Quiz) {
        val existingIndex = quizzes.indexOfFirst { it.id == quiz.id }
        if (existingIndex >= 0) {
            quizzes[existingIndex] = quiz
        } else {
            quizzes.add(quiz)
        }
    }

    override fun deleteQuiz(quizId: String) {
        quizzes.removeAll { it.id == quizId }
    }

    override fun loadAllQuizzes(): List<Quiz> = quizzes.toList()

    override fun loadQuiz(quizId: String): Quiz? = quizzes.find { it.id == quizId }
}
