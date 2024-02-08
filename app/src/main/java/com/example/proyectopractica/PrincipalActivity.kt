package com.example.proyectopractica

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PrincipalActivity : AppCompatActivity() {
    private lateinit var db :FirebaseFirestore

    private lateinit var adaptador : AdapterAvatar
    private lateinit var spinnerAdapter : ArrayAdapter<*>
    private lateinit var avatarButton : ImageButton
    private lateinit var seriesButton: Button
    private lateinit var moviesButton: Button
    private lateinit var moviesSeries: GridView

    private var item = 0

    private var movies = intArrayOf(R.drawable.pelicula_oppenhaimer,R.drawable.pelicula_sociedad_nieve,
        R.drawable.pelicula_asesinos_luna)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        db = FirebaseFirestore.getInstance()


        avatarButton = findViewById(R.id.avatarButton)
        seriesButton = findViewById(R.id.serieButton)
        moviesButton = findViewById(R.id.movieButton)
        moviesSeries = findViewById(R.id.moviesSeriesList)

        avatarButton.setOnClickListener{
            startActivity(Intent(this,UserActivity::class.java))
        }

        seriesButton.setOnClickListener{

        }

        moviesButton.setOnClickListener{

        }

    }

    private fun adaptarImagen() {
        moviesSeries.adapter = adaptador

        moviesSeries.setOnItemClickListener { _, _, position, _ ->
            item = movies[position]
            try {
                Toast.makeText(this, "Serie seleccionada: ${resources.getResourceEntryName(item)}", Toast.LENGTH_SHORT).show()

            }catch (e : Resources.NotFoundException){
                e.printStackTrace()
            }
        }
    }
}