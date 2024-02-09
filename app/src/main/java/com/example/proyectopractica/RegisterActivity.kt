package com.example.proyectopractica

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
//      Declaración de variables de Firebase
    private lateinit var fAuth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
//      Declaración de variables de objetos del Layout
    private lateinit var avatarGrid :GridView
    private lateinit var avatarImage :ImageView
    private lateinit var avatarAdapter : AdapterAvatar
    private lateinit var emailText: EditText
    private lateinit var userText :EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button
//      Declaración de variables empleadas para el avatar
    private var avatar :Int = 0
    private val list = intArrayOf(R.drawable.avatar_amarillo, R.drawable.avatar_azul,
        R.drawable.avatar_blanco, R.drawable.avatar_dorado, R.drawable.avatar_gris,
        R.drawable.avatar_morado, R.drawable.avatar_negro, R.drawable.avatar_rosa)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
//      Inicialización de variables de objetos del Layout
        avatarGrid = findViewById(R.id.avatarGrid)
        avatarImage = findViewById(R.id.avatarImage)
        emailText = findViewById(R.id.emailRegEditText)
        userText = findViewById(R.id.usernameRegEditText)
        passwordText = findViewById(R.id.passwordRegEditText)
        registerButton = findViewById(R.id.registerConfirmButton)
//      Inicialización de adaptador de avatares y variables de Firebase
        avatarAdapter = AdapterAvatar(this,list,150)
        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
//      Función para adaptación de imagenes de avatar
        adaptarImagen()
//      Cuando se pulsa el botón de registro comprueba si se puede registrar el usuario y almacenarlo correctamente
        registerButton.setOnClickListener {
            confirmReg()
        }
    }
/*
        Función para adaptar imágenes de avatar
            Según el objeto pulsado en el GridView, nos permite pillar el id de esa imagen y poder
            utilizarlo para poder modificar la imagen del avatar que tiene el usuario y así tener
            la referencia necesaria para pasar el id a Firestore cuando vamos a almacenar los
            datos del usuario.

            En caso de no tener recursos manda una excepción.
*/
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
/*
    Función para confirmar el registro del usuario
        Nos permite obtener lo escrito en los EditText del programa para poder registrar al usuario
        en Firebase y almacenar los datos necesarios en Firestore. Primero comprueba si se validan
        los campos introducidos (función validateInput) y en caso de estar correctos iría a
        comprobar la autenticación del usuario (función comprobarUsuario)
*/
    private fun confirmReg() {
        val username = userText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (validateInput(username, email, password)) {
            comprobarUsuario(username, email, password)
        }
    }
/*
    Función para comprobar la autenticación del usuario
        Nos permite comprobar si hay algún usuario con el mismo nombre de usuario, en caso de ser
        así saltará un mensaje para informar al usuario que debe modificar el nombre, si no hay
        ninguno con el mismo nombre procederemos a crear el usuario (función crearUsuario)
*/
    private fun comprobarUsuario(usernameF: String, emailF: String, passwordF: String){
        db.collection("users").whereEqualTo("username",usernameF)
            .get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Nombre de usuario en uso", Toast.LENGTH_SHORT).show()
                }
                else { crearUsuario(usernameF,emailF,passwordF) }
            }
            .addOnFailureListener{ Log.e("No usuario válido","Error al comprobar usuario") }
    }

/*
    Función para crear el usuario
        Nos permite autenticar y crear el usuario en Firebase, esta autenticación se hace con
        email y contraseña, para guardar el nombre de usuario y el id del avatar, mandaremos un
        mapa y el usuario que acabamos de crear a guardar (función guardarUsuario)
 */

    private fun crearUsuario(usernameC: String,emailC: String,passwordC: String) {
        fAuth.createUserWithEmailAndPassword(emailC, passwordC)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = fAuth.currentUser
                    val map = hashMapOf<String, Any>("username" to usernameC, "email" to emailC,
                                                    "avatarId" to avatar)
                    guardarUsuario(user, map)
                } else {
                    Log.w("No crear usuario", "Error en creación de usuario", task.exception)
                    Toast.makeText(this, "Error al crear usuario, cuenta de correo ya utilizada", Toast.LENGTH_LONG).show()
                }
            }
    }
/*
    Función para guardar usuario en Firestore
        Nos permite guardar el nombre del usuario, el mail (para comprobaciones posteriores) y el
        id del avatar en nuestra base de datos de Firestore. En caso de ser exitoso mandaría
        un mensaje al usuario mostrando que el registro ha sido correcto y se ha almacenado el
        usuario correctamente, mandandonos de nuevo a la pestaña de LogIn
 */
    private fun guardarUsuario(userS: FirebaseUser?, data: HashMap<String, Any>) {
        db.collection("users").document(userS!!.uid)
            .set(data)
            .addOnSuccessListener {
                Log.i("Usuario guardado", "Usuario almacenado correctamente")
                Toast.makeText(
                    this,
                    "Usuario creado y almacenado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Log.e("No usuario guardado", "Error al almacenar usuario",)
                Toast.makeText(this, "Error al almacenar usuario", Toast.LENGTH_SHORT).show()
                userS.delete().addOnSuccessListener {
                    Log.i("Usuario eliminado", "Usuario eliminado después del error de almacenamiento")
                }
            }
    }
/*
    Función de comprobación de campos válidos
        Comprueba si el nombre de usuario no está en blanco

        Comprueba si el mail está relleno, contiene un '@' y termina en los dominios aceptados

        Comprueba si la contraseña no está vacía, no contiene un símbolo, un número y una mayúscula

        Es decir si queremos poder registrar un usuario necesitamos
        - Campo de nombre con contenido
        - Campo de mail con un mail que tenga un '@' y acabe en los dominios aceptados
        - Campo de contraseña con una cadena de mínimo 8 caracteres, una mayúscula, un símbolo y un
          número
 */
    private fun validateInput(u: String, e: String, p: String): Boolean{
        when{
            u.isBlank() -> {
                userText.error = getString(R.string.NameError)
                userText.requestFocus()
                return false
            }
            p.isBlank() -> {
                passwordText.error = getString(R.string.PasswordBlankError)
                passwordText.requestFocus()
                return false
            }
            p.length < 8 -> {
                passwordText.error = getString(R.string.PasswordLengthError)
                passwordText.requestFocus()
                return false
            }

            !p.contains(Regex("[A-Z]")) -> {
                passwordText.error = getString(R.string.PasswordCapitalError)
                passwordText.requestFocus()
                return false

            }

            !p.contains(Regex("[0-9]")) -> {
                passwordText.error = getString(R.string.PasswordNumberError)
                passwordText.requestFocus()
                return false
            }

            !p.contains(Regex("[\\W_]")) -> {
                passwordText.error = getString(R.string.PasswordSimbolError)
                passwordText.requestFocus()
                return false
            }
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
            !e.matches(Regex(".+\\.(com|net|org|gov|edu|mil|int|arpa|eu|es)$")) -> {
                emailText.error = getString(R.string.EmailDomainError)
                emailText.requestFocus()
                return false
            }
            else -> return true
        }
    }
}
