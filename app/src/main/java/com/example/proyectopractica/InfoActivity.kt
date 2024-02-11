package com.example.proyectopractica

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class InfoActivity :AppCompatActivity(){

    private lateinit var texto: TextView
    private lateinit var imagen: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        texto = findViewById(R.id.titulo)
        imagen = findViewById(R.id.imagen)

        val obj = intent.getStringExtra("obj")
        var titulo = ""

        if (obj != null) {
            if(obj.startsWith("serie"))
                titulo = (obj.subSequence(6,obj.length).toString()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    .replace('_', ' ')
            else
                titulo = (obj.subSequence(9,obj.length).toString()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    .replace('_', ' ')

        }

        val id = resources.getIdentifier(obj,"drawable","com.example.proyectopractica")
        imagen.setImageResource(id)
        texto.text = titulo


        }
}