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
//  Declaración de variables de servicio Firebase y Firestore
    private lateinit var fAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
//  Declaración de elementos del layout
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
//  Inicialización de variables de servicio Firebase y Firestore
        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
//  Referenciación de elementos del layout
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.logInButton)
        usernameText = findViewById(R.id.usernameEditText)
        passwordText = findViewById(R.id.passwordEditText)
//  Función de inicio de sesión cuando se pulsa el botón
        loginButton.setOnClickListener {
            login()
        }
//  Cambio a actividad de registro
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
/*  Función de inicio de sesión
        Permite comprobar la autenticación del usuario
 */
    private fun login() {
    val username = usernameText.text.toString().trim()
    val password = passwordText.text.toString().trim()

    if (validateInput(username, password)) {
        comprobacionUsuarioExistente(username, password)
    }
}
/*
    Función de comprobación de usuario existente
        Si existe el usuario, comprueba si el email y contraseña coinciden en la
        autenticación de los mismos (función inicioSesionUsuario)
 */
    private fun comprobacionUsuarioExistente(usernameC: String, passwordC: String){
        db.collection("users").whereEqualTo("username", usernameC).get().addOnSuccessListener { fich ->
            if (!fich.isEmpty) {
                val email = fich.documents.first().getString("email")
                val avatarId = fich.documents.first().getLong("avatarId")
                inicioSesionUsuario(email!!,passwordC,avatarId!!)
            }
            else{ Log.e("EmailNotFound", "No se encontró el correo electrónico asociado al nombre de usuario.") }
        }
        .addOnFailureListener {
            Log.e("QueryError","Error de consulta")
        }
    }
/*
    Función para comprobar la autenticación del usuario existente
        Si la autenticación del correo y contraseña es correcta, manda al usuario un mensaje de
        éxito y lo envía a la pestaña principal
 */
    private fun inicioSesionUsuario(emailL: String, passwordL: String, avatariDL: Long){
        fAuth.signInWithEmailAndPassword(emailL, passwordL).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("SignIn", "Inicio de sesión correcto")
                Toast.makeText(this,"Inicio de sesión correcto",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PrincipalActivity::class.java)
                intent.putExtra("avatarId",avatariDL)
                startActivity(intent)
            } else {
                Log.w("SignInError", "Error de inicio de sesión")
            }
        }
    }
/*
    Función para validación de campos
        Comprueba si el los campos EditText de usuario y contraseña no están en blanco
 */
    private fun validateInput(u: String, p: String): Boolean{
        when {
            u.isBlank() -> {
                usernameText.error = getString(R.string.NameError)
                usernameText.requestFocus()
                return false
            }
            p.isBlank() -> {
                passwordText.error = getString(R.string.PasswordBlankError)
                passwordText.requestFocus()
                return false
            }
            else -> return true
        }
    }
}
