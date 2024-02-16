package com.example.proyectopractica

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrincipalActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var adaptador: AdapterAvatar
    private lateinit var avatarButton: ImageButton
    private lateinit var moviesSeries: GridView
    private lateinit var spinner: Spinner
    private var item = 0

    private var movies = intArrayOf(R.drawable.pelicula_oppenhaimer, R.drawable.pelicula_sociedad_nieve,
        R.drawable.pelicula_asesinos_luna, R.drawable.pelicula_hablame, R.drawable.pelicula_split,
        R.drawable.pelicula_quiet_place
    )

    private var series = intArrayOf(R.drawable.serie_reacher, R.drawable.serie_shameless,
        R.drawable.serie_black_mirror, R.drawable.serie_house_dragon, R.drawable.serie_percy_jackson,
        R.drawable.serie_the_boys
    )

    private var list = intArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        spinner = findViewById(R.id.spinner)
        avatarButton = findViewById(R.id.avatarButton)
        moviesSeries = findViewById(R.id.moviesSeriesList)

        adaptador = AdapterAvatar(this,list,300)

        adaptarImagen()
        modificacionLista()
        avatarUsuario()

        avatarButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
    }

    private fun modificacionLista(){
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                when (position){
                    0 -> adaptarLista(movies.clone()+series.clone())
                    1 -> adaptarLista(movies)
                    2 -> adaptarLista(series)
                }
            } override fun onNothingSelected(parent: AdapterView<*>?) {} }
    }

    private fun adaptarImagen() {
        moviesSeries.adapter = adaptador

        moviesSeries.setOnItemClickListener { _, _, position, _ ->
            item = list[position]
            try {
                val obj =  resources.getResourceEntryName(item)
                if(obj.startsWith("pelicula"))
                    Toast.makeText(this, obj.subSequence(9,obj.length), Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this,obj.subSequence(6,obj.length), Toast.LENGTH_SHORT).show()
                val intent = Intent(this,InfoActivity::class.java)
                intent.putExtra("obj",obj)
                startActivity(intent)
            }catch (e : Resources.NotFoundException){ e.printStackTrace() }
        }
    }

    private fun adaptarLista(l: IntArray){
        list = l
        adaptador = AdapterAvatar(this,l,300)
        moviesSeries.adapter = adaptador
    }

    private fun avatarUsuario(){


        val currentUser = firebaseAuth.currentUser
        currentUser.let { user ->

            val userRef = db.collection("users").document( user!!.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val avatarId = document.getLong("avatarId")
                    if (avatarId != null) {
                        try {
                            avatarButton.setImageResource(avatarId.toInt())
                        } catch (e: Resources.NotFoundException) { e.printStackTrace() }
                    }
                } else {
                    Log.d("No doc exists", "El documento del usuario no existe")
                }
            }.addOnFailureListener { e ->
                Log.e("User error", "Error al obtener el documento del usuario: ", e)
            }
        }
    }

}