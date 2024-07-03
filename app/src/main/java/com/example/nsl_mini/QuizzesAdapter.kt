package com.example.nsl_mini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class QuizzesAdapter(
    private val quizzes: List<Quiz>,
    private val onEditClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizzesAdapter.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.bind(quiz, onEditClick)
    }

    override fun getItemCount(): Int = quizzes.size

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.quizImageView)
        private val optionsTextView: TextView = itemView.findViewById(R.id.optionsTextView)
        private val correctAnswerTextView: TextView = itemView.findViewById(R.id.correctAnswerTextView)
        private val editButton: Button = itemView.findViewById(R.id.editButton)

        fun bind(quiz: Quiz, onEditClick: (Quiz) -> Unit) {
            Glide.with(itemView.context).load(quiz.imageUrl).into(imageView)
            optionsTextView.text = "Options: ${quiz.options.joinToString(", ")}"
            correctAnswerTextView.text = "Correct Answer: ${quiz.correctAnswer}"
            editButton.setOnClickListener { onEditClick(quiz) }
        }
    }
}
