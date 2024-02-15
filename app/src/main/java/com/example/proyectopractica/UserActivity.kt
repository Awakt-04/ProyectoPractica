package com.example.proyectopractica

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity :AppCompatActivity(){

    private lateinit var fAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var showUsername : TextView
    private lateinit var showEmail: TextView
    private lateinit var showPassword: TextView

    private var avatarId = 0
    private lateinit var user: FirebaseUser
    private var password = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        showUsername = findViewById(R.id.showUsername)
        showEmail = findViewById(R.id.showEmail)
        showPassword = findViewById(R.id.showPassword)

        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        user = fAuth.currentUser!!
        user.let { u ->

            val userRef = db.collection("users").document(u!!.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    avatarId = document.getLong("avatarId")!!.toInt()
                    showUsername.text = document.getString("username")!!
                    showEmail.text = document.getString("email")!!
                    password = ""
                }

            }
        }
    }
}
