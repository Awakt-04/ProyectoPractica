package com.example.proyectopractica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    // Declaración de variables para las vistas de la interfaz de usuario
    private lateinit var texto: TextView
    private lateinit var imagen: ImageView
    private lateinit var boton: Button
    private lateinit var sinopsisTextView: TextView

    // Método llamado cuando se crea la actividad
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Asignación de las vistas de la interfaz de usuario a las variables correspondientes
        sinopsisTextView = findViewById(R.id.sinopsis)
        texto = findViewById(R.id.titulo)
        imagen = findViewById(R.id.imagen)
        boton = findViewById(R.id.backInfoButton)

        // Obtención del objeto enviado desde la actividad anterior
        val obj = intent.getStringExtra("obj")
        var titulo = ""

        if (obj != null) {
            // Determinar si el objeto es una serie o una película y extraer el título
            titulo = if (obj.startsWith("serie")) {
                obj.substring(6)
            } else {
                obj.substring(9)
            }

            // Obtener el ID del recurso de string correspondiente a la sinopsis
            val sinopsisId = resources.getIdentifier(titulo, "string", packageName)

            if (sinopsisId != 0) {
                // Si se encuentra la sinopsis, asignarla al TextView correspondiente
                val sinopsis = getString(sinopsisId)
                sinopsisTextView.text = sinopsis
            } else {
                // Si no se encuentra la sinopsis, mostrar un mensaje de error
                sinopsisTextView.text = "Sinopsis no encontrada"
            }
        }

        // Obtener y asignar la imagen correspondiente al título
        val imageId = resources.getIdentifier(obj, "drawable", packageName)
        imagen.setImageResource(imageId)

        // Formatear el título y asignarlo al TextView correspondiente
        titulo = titulo.replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        texto.text = titulo

        // Configurar el evento click del botón para volver a la actividad principal
        boton.setOnClickListener {
            startActivity(Intent(this, PrincipalActivity::class.java))
        }
    }
}
