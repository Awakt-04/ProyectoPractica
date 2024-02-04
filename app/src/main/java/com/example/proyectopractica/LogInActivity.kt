package com.example.proyectopractica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        fAuth = FirebaseAuth.getInstance()

        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.logInButton)
        usernameText = findViewById(R.id.usernameEditText)
        passwordText = findViewById(R.id.passwordEditText)


        loginButton.setOnClickListener(){
            login()
        }

        register()

    }
    
    private fun register(){
        registerButton.setOnClickListener() {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun login(){
        var name = usernameText.text.toString()
        var password = passwordText.text.toString()
        var confirm = true

        if (name.isBlank()) {
            usernameText.error = getString(R.string.NameError)
            usernameText.requestFocus()
            confirm = false
        }

        confirm = passConfirm(password)

        if (confirm) {
            Log.i("OK LogIn","Inicio de sesión correcto")
            startActivity(Intent(this, PrincipalActivity::class.java))
        }
        else
            Log.i("Error LogIn","Inicio de sesión fallido")



    }

    private fun passConfirm(s: String): Boolean{
        when{
            s.isBlank() -> {
                passwordText.error = getString(R.string.PasswordBlankError)
                passwordText.requestFocus()
                return false
            }
            s.length < 8 -> {
                passwordText.error = getString(R.string.PasswordLengthError)
                passwordText.requestFocus()
                return false
            }
            !s.contains(Regex("[A-Z]")) -> {
                passwordText.error = getString(R.string.PasswordCapitalError)
                passwordText.requestFocus()
                return false
            }

            !s.contains(Regex("[0-9]")) -> {
                passwordText.error = getString(R.string.PasswordNumberError)
                passwordText.requestFocus()
                return false
            }

            !s.contains(Regex("[\\W_]")) -> {
                passwordText.error = getString(R.string.PasswordSimbolError)
                passwordText.requestFocus()
                return false
            }
            else -> return true
        }
    }


}