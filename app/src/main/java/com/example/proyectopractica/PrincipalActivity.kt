package com.example.proyectopractica

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class PrincipalActivity : AppCompatActivity() {

    private lateinit var adaptador: AdapterAvatar
    private lateinit var avatarButton: ImageButton
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

        spinner = findViewById(R.id.spinner)
        avatarButton = findViewById(R.id.avatarButton)
        moviesSeries = findViewById(R.id.moviesSeriesList)

        adaptador = AdapterAvatar(this,list,300)

//        val opciones = arrayOf("Peliculas y Series","Peliculas","Series")
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val opcionSeleccionada = opciones[position]
////                val listaFiltrada = filtrarListaImagenes(opcionSeleccionada)
//
////                adaptador = AdapterAvatar(this@PrincipalActivity, listaFiltrada, 300)
//                moviesSeries.adapter = adaptador
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//        }


        adaptarImagen()

        avatarButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
    }

    private fun adaptarImagen() {
        moviesSeries.adapter = adaptador

        moviesSeries.setOnItemClickListener { _, _, position, _ ->
            item = list[position]
            try {
                Toast.makeText(this, "Avatar seleccionado: ${resources.getResourceEntryName(item)}", Toast.LENGTH_SHORT).show()
            }catch (e : Resources.NotFoundException){
                e.printStackTrace()
            }
        }

    }

//    private fun filtrarListaImagenes(op :String):IntArray {
//        val lista : List<Int> = if (op == "Peliculas") {
//            list.filter { resources.getResourceEntryName(it).startsWith("pelicula_") }
//        } else (if (op == "Series") {
//            list.filter { resources.getResourceEntryName(it).startsWith("serie_") }
//        } else {
//            movies.clone() + series.clone()
//        }) as List<Int>
//        return lista.toIntArray()
//    }

}


