package com.example.pokeapirecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathernaut.R
import com.example.weathernaut.WeatherData
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter (private val weatherList: List<WeatherData>):RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val city: TextView
        val Temp: TextView
        val Sunrise: TextView
        val Sunset: TextView

        init {
            // Find our RecyclerView item's ImageView for future use
            city = view.findViewById(R.id.cityname)
            Temp = view.findViewById(R.id.temperature)
            Sunrise = view.findViewById(R.id.sunrise)
            Sunset = view.findViewById(R.id.sunset)
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
        holder.Temp.text = "Temperature: ${Data.temp}°F"
        holder.Sunrise.text ="Sunrise: ${formatTime(Data.sunrise)}"
        holder.Sunset.text = "Sunset: ${formatTime(Data.sunset)}"
    }

    private fun formatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val date = Date(timestamp * 1000)
        return dateFormat.format(date)
    }
}




