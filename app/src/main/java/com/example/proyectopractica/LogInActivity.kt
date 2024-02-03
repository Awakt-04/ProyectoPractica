package com.example.proyectopractica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LogInActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.logInButton)

        this.register()
        this.login()
    }
    
    private fun register(){
        registerButton.setOnClickListener() {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun login(){
        loginButton.setOnClickListener(){
            startActivity(Intent(this,PrincipalActivity::class.java))
        }
    }


}