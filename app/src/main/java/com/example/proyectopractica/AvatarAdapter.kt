package com.example.proyectopractica

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

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
        } else { convertView as ImageView
        }

        imagen.setImageResource(list[position])
        return imagen
    }
}