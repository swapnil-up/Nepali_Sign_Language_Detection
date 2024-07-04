package com.example.nsl_mini

import UserData
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nsl_mini.databinding.ActivityAdminBinding
import com.google.firebase.database.*

class AdminActivity : BaseActivityAdmin() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userList: MutableList<UserData>
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawerAdmin()
        userList = mutableListOf()
        userAdapter = UserAdapter(userList)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = userAdapter

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        // Fetch user data from Firebase
        fetchUserData()
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
                // Sort the list by ID in descending order (assuming ID is a timestamp)
                userList.sortByDescending { it.id }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onBackPressed() {
        // Call super.onBackPressed to ensure default back button behavior
        super.onBackPressed()

        // Finish the activity and exit the app
        finishAffinity()
    }
}
