package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewQuizzesActivity : AppCompatActivity() {
    private lateinit var quizzesRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var quizzesAdapter: QuizzesAdapter
    private lateinit var quizList: MutableList<Quiz>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_quizzes)

        quizzesRecyclerView = findViewById(R.id.quizzesRecyclerView)
        quizzesRecyclerView.layoutManager = LinearLayoutManager(this)
        quizList = mutableListOf()
        quizzesAdapter = QuizzesAdapter(quizList) { quiz ->
            val intent = Intent(this, AddQuizActivity::class.java)
            intent.putExtra("quizId", quiz.id)
            intent.putExtra("imageUrl", quiz.imageUrl)
            intent.putStringArrayListExtra("options", ArrayList(quiz.options))
            intent.putExtra("correctAnswer", quiz.correctAnswer)
            startActivity(intent)
        }
        quizzesRecyclerView.adapter = quizzesAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("quizzes")
        loadQuizzes()
    }

    private fun loadQuizzes() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                quizList.clear()
                for (quizSnapshot in snapshot.children) {
                    val quiz = quizSnapshot.getValue(Quiz::class.java)
                    quiz?.let {
                        it.id = quizSnapshot.key
                        quizList.add(it)
                    }
                }
                quizzesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewQuizzesActivity, "Failed to load quizzes", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
