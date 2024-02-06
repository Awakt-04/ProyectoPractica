package com.example.proyectopractica

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var fAuth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    private lateinit var avatarGrid :GridView
    private lateinit var avatarImage :ImageView
    private lateinit var avatarAdapter : AdapterAvatar
    private lateinit var emailText: EditText
    private lateinit var userText :EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button

    private var avatar :Int = 0
    private val list = intArrayOf(R.drawable.avatar_amarillo, R.drawable.avatar_azul,
        R.drawable.avatar_blanco, R.drawable.avatar_dorado, R.drawable.avatar_gris,
        R.drawable.avatar_morado, R.drawable.avatar_negro, R.drawable.avatar_rosa)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        avatarGrid = findViewById(R.id.avatarGrid)
        avatarImage = findViewById(R.id.avatarImage)
        emailText = findViewById(R.id.emailRegEditText)
        userText = findViewById(R.id.usernameRegEditText)
        passwordText = findViewById(R.id.passwordRegEditText)
        registerButton = findViewById(R.id.registerConfirmButton)

        avatarAdapter = AdapterAvatar(this,list)
        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        adaptarImagen()

        registerButton.setOnClickListener {
            confirmReg()
        }


    }
    private fun adaptarImagen() {
        avatarGrid.adapter = avatarAdapter

        avatarGrid.setOnItemClickListener { _, _, position, _ ->
            avatar = list[position]
            avatarImage.setImageResource(avatar)
            try {
                Toast.makeText(this, "Avatar seleccionado: ${resources.getResourceEntryName(avatar)}", Toast.LENGTH_SHORT).show()
            }catch (e : Resources.NotFoundException){
                e.printStackTrace()
            }
        }

    }

    private fun confirmReg(){
        val username = userText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (username.isBlank()) {
            userText.error = getString(R.string.NameError)
            userText.requestFocus()
            return
        }
        if(!emailCheck(email))
            return
        if (!passCheck(password))
            return


        Log.i("OK Registro","Registro correcto")

        fAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = fAuth.currentUser
                    val map = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "avatarId" to avatar
                    )

                    db.collection("users").document(user!!.uid)
                        .set(map)
                        .addOnSuccessListener {
                            Log.i("DB user", "Usuario almacenado correctamente")
                            Toast.makeText(this, "Usuario creado y almacenado correctamente", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LogInActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("DB user error", "Error al almacenar usuario", e)
                            Toast.makeText(this, "Error al almacenar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                            // Elimina el usuario recién creado si falla el almacenamiento en Firestore
                            user.delete().addOnSuccessListener {
                                Log.i("User deletion", "Usuario eliminado después del error de almacenamiento")
                            }
                        }
                } else {
                    Log.e("User creation error", "Error al crear usuario", task.exception)
                    Toast.makeText(this, "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun validateInput(u: String, e: String, p: String): Boolean{
        when{



            e.isBlank() -> {
                emailText.error = getString(R.string.EmailBlankError)
                emailText.requestFocus()
                return false
            }
            !e.contains("@") -> {
                emailText.error = getString(R.string.EmailAtError)
                emailText.requestFocus()
                return false
            }
            !e.matches(Regex(".+\\.(com|net|org|gov|edu|mil|int|arpa|eu)$")) -> {
                emailText.error = getString(R.string.EmailDomainError)
                emailText.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun emailCheck(s:String): Boolean{
        when{
            s.isBlank() -> {
                emailText.error = getString(R.string.EmailBlankError)
                emailText.requestFocus()
                return false
            }
            !s.contains("@") -> {
                emailText.error = getString(R.string.EmailAtError)
                emailText.requestFocus()
                return false
            }
            !s.matches(Regex(".+\\.(com|net|org|gov|edu|mil|int|arpa|eu)$")) -> {
                emailText.error = getString(R.string.EmailDomainError)
                emailText.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun passCheck(s: String): Boolean{
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

class AdapterAvatar(
    private val context: Context,
    private val list: IntArray
)  : BaseAdapter(){

    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int): Any {
        return list[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imagen = if (convertView == null) {
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(150, 150)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        } else { convertView as ImageView }

        imagen.setImageResource(list[position])
        return imagen
    }
}
