package com.example.nsl_mini

import UserData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList: List<UserData>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val snTextView: TextView = itemView.findViewById(R.id.snTextView)
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val roleTextView: TextView = itemView.findViewById(R.id.roleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.snTextView.text = (position + 1).toString()
        holder.usernameTextView.text = user.username
        holder.phoneNumberTextView.text = user.phoneNumber
        holder.roleTextView.text = user.role
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
