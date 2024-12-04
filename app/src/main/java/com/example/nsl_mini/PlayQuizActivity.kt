package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.SharedPreferences

class PlayQuizActivity : BaseActivity() {
    private lateinit var imageView: ImageView
    private lateinit var optionButtons: List<Button>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userProgressReference: DatabaseReference
    private lateinit var quizList: MutableList<Quiz>
    private var currentQuizIndex = 0
    private var correctAnswers = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_quiz)
        setupDrawer()
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        userId = getUserId()

        imageView = findViewById(R.id.imageView)
        optionButtons = listOf(
            findViewById(R.id.optionButton1),
            findViewById(R.id.optionButton2),
            findViewById(R.id.optionButton3),
            findViewById(R.id.optionButton4)
        )
        databaseReference = FirebaseDatabase.getInstance().reference.child("quizzes")
        userProgressReference = FirebaseDatabase.getInstance().reference.child("userProgress").child(userId)

        loadUserProgress()
        loadQuizzes()
    }

    private fun loadUserProgress() {
        userProgressReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentQuizIndex = snapshot.child("currentQuizIndex").getValue(Int::class.java) ?: 0
                    correctAnswers = snapshot.child("correctAnswers").getValue(Int::class.java) ?: 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PlayQuizActivity, "Failed to load user progress", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadQuizzes() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                quizList = snapshot.children.mapNotNull { it.getValue(Quiz::class.java) }.toMutableList()
                resetUserProgressIfNeeded()
                showNextQuiz()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PlayQuizActivity, "Failed to load quizzes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetUserProgressIfNeeded() {
        // Reset user progress if currentQuizIndex is out of range
        if (currentQuizIndex >= quizList.size) {
            currentQuizIndex = 0
            correctAnswers = 0
            updateUserProgress()
        }
    }

    private fun showNextQuiz() {
        if (currentQuizIndex < quizList.size) {
            val quiz = quizList[currentQuizIndex]
            Glide.with(this).load(quiz.imageUrl).into(imageView)

            optionButtons.forEachIndexed { index, button ->
                button.text = quiz.options[index]
                button.setOnClickListener {
                    checkAnswer(button.text.toString())
                }
            }
        } else {
            Toast.makeText(this, "Quiz completed!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, QuizCompletedActivity::class.java))
            finish()
        }
    }

    private fun checkAnswer(selectedOption: String) {
        val correctAnswer = quizList[currentQuizIndex].correctAnswer

        if (selectedOption == correctAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            correctAnswers++
            currentQuizIndex++
            updateUserProgress()
            showNextQuiz()
        } else {
            Toast.makeText(this, "Incorrect! The correct answer is $correctAnswer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserProgress() {
        val progressData = mapOf(
            "currentQuizIndex" to currentQuizIndex,
            "correctAnswers" to correctAnswers
        )
        userProgressReference.setValue(progressData)
    }

    private fun getUserId(): String {
        return sharedPreferences.getString("user_id", "") ?: ""
    }

    override fun onBackPressed() {
        // Navigate to MainActivity explicitly
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        // Call super to handle default back button behavior
        super.onBackPressed()
    }
}
