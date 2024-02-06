package com.example.proyectopractica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogInActivity : AppCompatActivity() {
// declaración de variables de servicio Firebase y Firestore
    private lateinit var fAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
// declaración de elementos del layout
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
// inicialización de variables de servicio Firebase y Firestore
        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
// referenciación de elementos del layout
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.logInButton)
        usernameText = findViewById(R.id.usernameEditText)
        passwordText = findViewById(R.id.passwordEditText)
// función de inicio de sesión cuando se pulsa el botón
        loginButton.setOnClickListener {
            login()
        }
// cambio a actividad de registro
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
// función de inicio de sesión
    private fun login() {
// declaración e inicialización de variables
        val username = usernameText.text.toString().trim()
        val password = passwordText.text.toString().trim()
// comprobación campo usuario nulo
        if (username.isEmpty()) {
            usernameText.error = getString(R.string.NameError)
            usernameText.requestFocus()
            return
        }
// comprobación campo contraseña nulo
        if (password.isEmpty()) {
            passwordText.error = getString(R.string.PasswordBlankError)
            passwordText.requestFocus()
            return
        }
// obtención documento usuario con mismo nombre del introducido en el campo
        db.collection("users").whereEqualTo("username", username).get()
// si éxito al encontrar al usuario comprueba si el documento está vacío
            .addOnSuccessListener { fich ->
                if (!fich.isEmpty) {
// si tiene contenido el documento, obtiene el email
                    val email = fich.documents.first().getString("email")
// si email encontrado
                    if (email != null) {
// comprobación de inicio de sesión de usuario autorizado (registrado)
                        fAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
// si usuario autorizado manda mensaje de éxito y cambia a actividad principal
                                if (task.isSuccessful) {
                                    Log.d("SignIn", "Inicio de sesión correcto")
                                    Toast.makeText(this,"Inicio de sesión correcto",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, PrincipalActivity::class.java))
// si usuario no autorizado log de error de inicio de sesión
                                } else {
                                    Log.w("SignInError", "Error de inicio de sesión")
                                }
                            }
// si email no encontrado log de error de mail
                    } else{ Log.e("EmailNotFound", "No se encontró el correo electrónico asociado al nombre de usuario.") }
                }
            }
// si consulta fallida log de error de consulta
            .addOnFailureListener {
                Log.e("QueryError","Error de consulta")
            }
    }
}
