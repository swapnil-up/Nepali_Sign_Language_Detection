package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewQuizzesActivity : BaseActivity() {
    private lateinit var quizzesRecyclerView: RecyclerView
    private lateinit var quizzesAdapter: QuizzesAdapter
    private lateinit var quizList: MutableList<Quiz>
    private lateinit var storage: QuizStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_quizzes)

        storage = LocalQuizStorage(this)

        quizzesRecyclerView = findViewById(R.id.quizzesRecyclerView)
        quizzesRecyclerView.layoutManager = LinearLayoutManager(this)
        quizList = mutableListOf()
        quizzesAdapter = QuizzesAdapter(quizList, { quiz ->
            val intent = Intent(this, AddQuizActivity::class.java)
            intent.putExtra("quizId", quiz.id)
            intent.putExtra("imageUrl", quiz.imageUrl)
            intent.putStringArrayListExtra("options", ArrayList(quiz.options))
            intent.putExtra("correctAnswer", quiz.correctAnswer)
            startActivity(intent)
        }, { quiz ->
            deleteQuiz(quiz)
        })
        quizzesRecyclerView.adapter = quizzesAdapter

        loadQuizzes()
    }

    override fun onResume() {
        super.onResume()
        loadQuizzes()
    }

    private fun loadQuizzes() {
        quizList.clear()
        quizList.addAll(storage.loadAllQuizzes())
        quizzesAdapter.notifyDataSetChanged()
        if (quizList.isEmpty()) {
            Toast.makeText(this, "No quizzes yet. Add one!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteQuiz(quiz: Quiz) {
        quiz.id?.let {
            storage.deleteQuiz(it)
            loadQuizzes()
            Toast.makeText(this, "Quiz deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, AddQuizActivity::class.java)
        startActivity(intent)
        finish()
    }
}
