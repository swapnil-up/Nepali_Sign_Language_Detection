package com.example.nsl_mini


import UserData
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nsl_mini.databinding.ActivityAdminBinding // Correct package name
import com.google.firebase.database.*

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userList: MutableList<UserData>
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userList = mutableListOf()
        userAdapter = UserAdapter(userList)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = userAdapter

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        // Fetch user data from Firebase
        fetchUserData()

        Toast.makeText(this, "Hello Admin", Toast.LENGTH_SHORT).show()
    }

    private fun fetchUserData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserData::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
