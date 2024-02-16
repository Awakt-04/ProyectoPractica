package com.example.proyectopractica

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity :AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var cUser: FirebaseUser

    private lateinit var showUsername: TextView
    private lateinit var showEmail: TextView
    private lateinit var showPassword: TextView
    private lateinit var showAvatarImage: ImageButton

    private lateinit var usernameInput: TextInputLayout
    private lateinit var emailInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout

    private lateinit var backButton: Button

    private var avatarId = 0L
    private var username = ""
    private var email = ""
    private var password = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        usernameInput = findViewById(R.id.changeUsername)
        emailInput = findViewById(R.id.changeEmail)
        passwordInput = findViewById(R.id.changePassword)
        showAvatarImage = findViewById(R.id.showAvatarImage)
        showUsername = findViewById(R.id.showUsername)
        showEmail = findViewById(R.id.showEmail)
        showPassword = findViewById(R.id.showPassword)
        backButton = findViewById(R.id.backButton)

        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        obtenerUsuario()

        cambioDatos()

    }

    private fun cambioDatos() {
        showUsername.setOnClickListener {
            showUsername.visibility = View.GONE
            usernameInput.visibility = View.VISIBLE

        }

        showPassword.setOnClickListener {
            showPassword.visibility = View.GONE
            passwordInput.visibility = View.VISIBLE
        }

        showEmail.setOnClickListener {
            showEmail.visibility = View.GONE
            emailInput.visibility = View.VISIBLE
        }

        backButton.setOnClickListener {
            confirmUpdate()
        }

    }

    @Suppress("DEPRECATION")
    private fun confirmUpdate() {
        val username2 = input(usernameInput, username)
        val email2 = input(emailInput, email)
        val password2 = input(passwordInput, password)

        if (validateInput(username2, email2, password2))
            cUser.let { user ->
                val userRef = db.collection("users").document(user!!.uid)

                val updates = mutableMapOf<String, Any>()

                if (email2.isNotEmpty() && email2 != email) {
                    updates["email"] = email2
                    cUser.updateEmail(email2)
                }

                if (username2.isNotEmpty() && username2 != username) {
                    updates["username"] = username2
                }

                if (password2.isNotEmpty() && password2 != password) {
                    updates["password"] = password2
                }

                if (updates.isNotEmpty()) {
                    userRef.update(updates)
                }
            }


    }

    private fun obtenerUsuario() {
        cUser = fAuth.currentUser!!
        cUser.let { user ->

            val userRef = db.collection("users").document(user!!.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    avatarId = document.getLong("avatarId")!!
                    username = document.getString("username")!!
                    password = document.getString("password")!!
                    email = document.getString("email")!!
                    try {
                        showAvatarImage.setImageResource(avatarId.toInt())
                        showAvatarImage.apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }

                        showUsername.text = username
                        showPassword.text = password
                        showEmail.text = email
                    } catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    Log.d("No doc exists", "El documento del usuario no existe")
                }
            }.addOnFailureListener { e ->
                Log.e("User error", "Error al obtener el documento del usuario: ", e)
            }
        }
    }

    private fun input(layout: TextInputLayout, org: String): String {
        if (layout.isVisible) {
            val txt = layout.editText?.text.toString()
            if (txt != org) {
                return txt
            }
        }
        layout.requestFocus()
        return ""
    }

    private fun validateInput(u: String, e: String, p: String): Boolean {
        var isValid: Boolean = false

        when {
            usernameInput.isVisible -> {
                when {
                    u.isBlank() -> {
                        usernameInput.editText?.error = getString(R.string.NameError)
                        usernameInput.editText?.requestFocus()
                        isValid =  false
                    }
                }
            }

            passwordInput.isVisible -> {
                when {
                    p.isBlank() -> {
                        passwordInput.editText?.error = getString(R.string.PasswordBlankError)
                        passwordInput.editText?.requestFocus()
                        isValid =  false
                    }

                    p.length < 8 -> {
                        passwordInput.editText?.error = getString(R.string.PasswordLengthError)
                        passwordInput.editText?.requestFocus()
                        isValid =  false
                    }

                    !p.contains(Regex("[A-Z]")) -> {
                        passwordInput.editText?.error = getString(R.string.PasswordCapitalError)
                        passwordInput.editText?.requestFocus()
                        isValid =  false

                    }

                    !p.contains(Regex("[0-9]")) -> {
                        passwordInput.editText?.error = getString(R.string.PasswordNumberError)
                        passwordInput.editText?.requestFocus()
                        isValid =  false
                    }

                    !p.contains(Regex("[\\W_]")) -> {
                        passwordInput.editText?.error = getString(R.string.PasswordSimbolError)
                        passwordInput.editText?.requestFocus()
                        isValid =  false
                    }
                }
            }

            emailInput.isVisible -> {
                when {
                    e.isBlank() -> {
                        emailInput.editText?.error = getString(R.string.EmailBlankError)
                        emailInput.editText?.requestFocus()
                        isValid =  false
                    }

                    !e.contains("@") -> {
                        emailInput.editText?.error = getString(R.string.EmailAtError)
                        emailInput.editText?.requestFocus()
                        isValid =  false
                    }

                    !e.matches(Regex(".+\\.(com|net|org|gov|edu|mil|int|arpa|eu|es)$")) -> {
                        emailInput.editText?.error = getString(R.string.EmailDomainError)
                        emailInput.editText?.requestFocus()
                        isValid =  false
                    }
                }
            }

            else -> isValid =  true
        }

        return isValid
    }
}