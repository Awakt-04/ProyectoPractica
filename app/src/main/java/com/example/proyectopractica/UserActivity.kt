package com.example.proyectopractica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var cUser: FirebaseUser

    private lateinit var showUsername: TextView
    private lateinit var showEmail: TextView
    private lateinit var showPassword: TextView
    private lateinit var showAvatarImage: ImageButton
    private lateinit var linearLayout: LinearLayout

    private lateinit var usernameInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout

    private lateinit var avatarImage: ImageView
    private lateinit var avatarGrid: GridView
    private lateinit var adapter: AdapterAvatar

    private lateinit var backButton: Button

    private var avatarId = 0L
    private var avatar = 0
    private var username = ""
    private var email = ""
    private var password = ""

    private val list = intArrayOf(R.drawable.avatar_amarillo, R.drawable.avatar_azul,
        R.drawable.avatar_blanco, R.drawable.avatar_dorado, R.drawable.avatar_gris,
        R.drawable.avatar_morado, R.drawable.avatar_negro, R.drawable.avatar_rosa)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        usernameInput = findViewById(R.id.changeUsername)
        passwordInput = findViewById(R.id.changePassword)
        showAvatarImage = findViewById(R.id.showAvatarImage)
        showUsername = findViewById(R.id.showUsername)
        showEmail = findViewById(R.id.showEmail)
        showPassword = findViewById(R.id.showPassword)
        backButton = findViewById(R.id.backButton)
        avatarImage = findViewById(R.id.fieldAvatarImage)
        avatarGrid = findViewById(R.id.changeAvatarImage)
        linearLayout = findViewById(R.id.linearLayout)

        adapter = AdapterAvatar(this, list, 150)
        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        avatar = adaptarImagen()

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

        showAvatarImage.setOnClickListener {
            showAvatarImage.visibility = View.GONE
            linearLayout.visibility = View.VISIBLE
        }

        backButton.setOnClickListener {
            confirmUpdate()
        }
    }

    private fun obtenerUsuario() {
        cUser = fAuth.currentUser!!
        val userRef = db.collection("users").document(cUser.uid)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                avatarId = document.getLong("avatarId") ?: 0
                username = document.getString("username") ?: ""
                password = document.getString("password") ?: ""
                email = document.getString("email") ?: ""

                showAvatarImage.setImageResource(avatarId.toInt())
                showAvatarImage.scaleType = ImageView.ScaleType.CENTER_CROP

                showUsername.text = username
                showPassword.text = password
                showEmail.text = email
            } else {
                Log.d("No doc exists", "El documento del usuario no existe")
            }
        }.addOnFailureListener { e ->
            Log.e("User error", "Error al obtener el documento del usuario: ", e)
        }
    }

    private fun input(layout: TextInputLayout): String {
        return if (layout.isVisible) {
            layout.editText?.text.toString()
        } else {
            layout.requestFocus()
            layout.error = layout.hint.toString()
            ""
        }
    }

    private fun confirmUpdate() {
        val username2 = input(usernameInput)
        val password2 = input(passwordInput)

        if (validateInput(username2,  password2)) {
            val updates = mutableMapOf<String, Any>()

            if (username2.isNotEmpty() && username2 != username) {
                updates["username"] = username2
            }

            if (password2.isNotEmpty() && password2 != password) {
                updates["password"] = password2
            }

            // Actualizar avatar solo si ha cambiado
            if (avatarId != avatar.toLong()) {
                updates["avatarId"] = avatar.toLong()
            }

            if (updates.isNotEmpty()) {
                val userRef = db.collection("users").document(cUser.uid)
                userRef.update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "La actualización fue exitosa",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Actualizar avatar en la interfaz de usuario
                        if (avatarId != avatar.toLong()) {
                            showAvatarImage.setImageResource(avatar)
                            avatarId = avatar.toLong()
                        }

                        // Ir a la actividad principal
                        startActivity(Intent(this, PrincipalActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("User error", "Error al actualizar los datos del usuario: ", e)
                        Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "No hay cambios para actualizar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }




    private fun validateInput(u: String, p: String): Boolean {
        var isValid = true

        when {
            usernameInput.isVisible -> {
                when {
                    u.isBlank() -> {
                        usernameInput.error = getString(R.string.NameError)
                        isValid = false
                    }
                }
            }

            passwordInput.isVisible -> {
                when {
                    p.isBlank() -> {
                        passwordInput.error = getString(R.string.PasswordBlankError)
                        isValid = false
                    }
                    p.length < 8 -> {
                        passwordInput.error = getString(R.string.PasswordLengthError)
                        isValid = false
                    }
                    !p.contains(Regex("[A-Z]")) -> {
                        passwordInput.error = getString(R.string.PasswordCapitalError)
                        isValid = false
                    }
                    !p.contains(Regex("[0-9]")) -> {
                        passwordInput.error = getString(R.string.PasswordNumberError)
                        isValid = false
                    }
                    !p.contains(Regex("[\\W_]")) -> {
                        passwordInput.error = getString(R.string.PasswordSimbolError)
                        isValid = false
                    }
                }
            }

        }
        return isValid
    }

    private fun adaptarImagen(): Int {
        avatarGrid.adapter = adapter

        avatarGrid.setOnItemClickListener { _, _, position, _ ->
            avatar = list[position]
            avatarImage.setImageResource(avatar)
            try {
                Log.d("idImagen", avatar.toString())
                Toast.makeText(
                    this,
                    "Avatar seleccionado: ${resources.getResourceEntryName(avatar)}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return list[0] // Devolver la primera imagen por defecto
    }
}
