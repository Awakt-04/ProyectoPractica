package com.example.proyectopractica

import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.FirebaseFirestore

class PrincipalActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    private lateinit var adaptador: AdapterAvatar
    private lateinit var avatarButton: ImageButton
    private lateinit var seriesButton: Button
    private lateinit var moviesButton: Button
    private lateinit var moviesSeries: GridView
    private lateinit var spinner: Spinner
    private var item = 0

    private var movies = intArrayOf(
        R.drawable.pelicula_oppenhaimer, R.drawable.pelicula_sociedad_nieve,
        R.drawable.pelicula_asesinos_luna, R.drawable.pelicula_hablame, R.drawable.pelicula_split,
        R.drawable.pelicula_quiet_place
    )

    private var series = intArrayOf(
        R.drawable.serie_reacher,
        R.drawable.serie_shameless,
        R.drawable.serie_black_mirror,
        R.drawable.serie_house_dragon,
        R.drawable.serie_percy_jackson,
        R.drawable.serie_the_boys
    )

    private var list = (movies.clone() + series.clone())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)


//        db = FirebaseFirestore.getInstance()
//        db.collection("users").get(user.uid)

        spinner = findViewById(R.id.spinner)
        avatarButton = findViewById(R.id.avatarButton)
        seriesButton = findViewById(R.id.serieButton)
        moviesButton = findViewById(R.id.movieButton)
        moviesSeries = findViewById(R.id.moviesSeriesList)

        adaptador = AdapterAvatar(this, list)

//        adaptarImagen()

        avatarButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }

        seriesButton.setOnClickListener {
            adaptarImagenSeries()
        }

        moviesButton.setOnClickListener {
            adaptarImagenMovies()
        }
    }

    private fun adaptarImagenSeries() {
        moviesSeries.adapter = adaptador
        moviesSeries.setOnItemClickListener { _, _, position, _ ->
            item = list[position]
            try {
                Toast.makeText(
                    this,
                    "Serie seleccionada: ${resources.getResourceEntryName(item)}",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
        }

    }


//        fun getView(position: Int, convertView: View?, parent: ViewGroup?): View{
//            val imagen = if (convertView == null) {
//                ImageView(this).apply {
//                    layoutParams = ViewGroup.LayoutParams(200, 200)
//                    scaleType = ImageView.ScaleType.CENTER_CROP
//                }
//            } else { convertView as ImageView
//            }
//
//            imagen.setImageResource(list[position])
//            return imagen
//        }
    }

    private fun adaptarImagenSeries() {
        moviesSeries.adapter = adaptador
        moviesSeries.setOnItemClickListener { _, _, position, _ ->
            item = series[position]
            try {
                Toast.makeText(
                    this,
                    "Serie seleccionada: ${resources.getResourceEntryName(item)}",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
        }

    }

    private fun adaptarImagenMovies() {
        moviesSeries.adapter = adaptador
        moviesSeries.setOnItemClickListener { _, _, position, _ ->
            item = movies[position]
            try {
                Toast.makeText(
                    this,
                    "Serie seleccionada: ${resources.getResourceEntryName(item)}",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
        }

    }


}
