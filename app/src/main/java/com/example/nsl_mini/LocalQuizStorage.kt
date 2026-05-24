package com.example.nsl_mini

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class LocalQuizStorage(context: Context) : QuizStorage {

    private val prefs: SharedPreferences = context.getSharedPreferences("local_quizzes", Context.MODE_PRIVATE)

    override fun saveQuiz(quiz: Quiz) {
        val quizzes = loadAllQuizzes().toMutableList()
        val existingIndex = quizzes.indexOfFirst { it.id == quiz.id }
        if (existingIndex >= 0) {
            quizzes[existingIndex] = quiz
        } else {
            quizzes.add(quiz)
        }
        saveAll(quizzes)
    }

    override fun deleteQuiz(quizId: String) {
        val quizzes = loadAllQuizzes().toMutableList()
        quizzes.removeAll { it.id == quizId }
        saveAll(quizzes)
    }

    override fun loadAllQuizzes(): List<Quiz> {
        val json = prefs.getString("quizzes", "[]") ?: "[]"
        val arr = JSONArray(json)
        return (0 until arr.length()).map { parseQuiz(arr.getJSONObject(it)) }
    }

    override fun loadQuiz(quizId: String): Quiz? {
        return loadAllQuizzes().find { it.id == quizId }
    }

    private fun saveAll(quizzes: List<Quiz>) {
        val arr = JSONArray()
        quizzes.forEach { arr.put(toJson(it)) }
        prefs.edit().putString("quizzes", arr.toString()).apply()
    }

    private fun toJson(quiz: Quiz): JSONObject {
        return JSONObject().apply {
            put("id", quiz.id ?: UUID.randomUUID().toString())
            put("imagePath", quiz.imageUrl ?: "")
            put("options", JSONArray(quiz.options))
            put("correctAnswer", quiz.correctAnswer ?: "")
        }
    }

    private fun parseQuiz(obj: JSONObject): Quiz {
        val optionsArr = obj.optJSONArray("options")
        val options = mutableListOf<String>()
        if (optionsArr != null) {
            for (i in 0 until optionsArr.length()) {
                options.add(optionsArr.getString(i))
            }
        }
        return Quiz(
            id = obj.optString("id"),
            imageUrl = obj.optString("imagePath"),
            options = options,
            correctAnswer = obj.optString("correctAnswer")
        )
    }

    companion object {
        fun saveImageToInternalStorage(context: Context, sourceUri: android.net.Uri, quizId: String): String? {
            return try {
                val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
                val dir = File(context.filesDir, "quiz_images")
                dir.mkdirs()
                val file = File(dir, "${quizId}.jpg")
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }
}
