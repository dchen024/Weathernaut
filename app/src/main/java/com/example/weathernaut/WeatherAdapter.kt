package com.example.pokeapirecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.weathernaut.R
import com.example.weathernaut.WeatherData
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter (private val weatherList: List<WeatherData>):RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val city: TextView
        val temp: TextView
        val icon: ImageView

        init {
            // Find our RecyclerView item's ImageView for future use
            city = view.findViewById(R.id.cityname)
            temp = view.findViewById(R.id.temperature)
            icon = view.findViewById(R.id.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = weatherList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Data = weatherList[position]
        holder.city.text = Data.Cityname
        holder.temp.text = "${Data.temp}°F"

    }

}




