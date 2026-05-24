package com.example.nsl_mini

interface QuizStorage {
    fun saveQuiz(quiz: Quiz)
    fun deleteQuiz(quizId: String)
    fun loadAllQuizzes(): List<Quiz>
    fun loadQuiz(quizId: String): Quiz?
}
