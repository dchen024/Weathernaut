package com.example.weathernaut

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.example.weathernaut.MyPreferences.Companion.addCity
import com.example.weathernaut.MyPreferences.Companion.getCities
import com.example.weathernaut.MyPreferences.Companion.removeCity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailedActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val url = "https://api.openweathermap.org/data/2.5/weather"
    private val apiKey = "13e14e0c8b68f04c958f100877a0a805"

    private lateinit var cityNameTextView: TextView
    private lateinit var tempTextView: TextView
    private lateinit var sunsetTextView: TextView
    private lateinit var sunriseTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var feelslikeTextView: TextView
    private lateinit var tempminTextView: TextView
    private lateinit var tempmaxTextView: TextView
    private lateinit var iconImageView: ImageView

    private lateinit var favoriteButton: Button
    private lateinit var removeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        //receives city name from MainActivity
        val city_name = intent.getStringExtra("CITY_NAME").toString()

        tempTextView = findViewById(R.id.tempTextView)
        sunsetTextView = findViewById(R.id.sunsetTextView)
        sunriseTextView = findViewById(R.id.sunriseTextView)
        cityNameTextView = findViewById(R.id.cityName)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        feelslikeTextView = findViewById(R.id.feels_likeTextView)
        tempminTextView = findViewById(R.id.temp_minTextView)
        tempmaxTextView = findViewById(R.id.temp_maxTextView)
        iconImageView = findViewById(R.id.icon)

        favoriteButton = findViewById(R.id.favoriteButton)
        favoriteButton.setOnClickListener {
            favoriteCity(city_name)
        }

        searchCityWeather(city_name)

        /** Configures favorite button to represent its function */
        if(getCities(this).contains(city_name)){
            favoriteButton.text = getString(R.string.unfavoriteButton)
        }
        else{
            favoriteButton.text = getString(R.string.favoriteButton)
        }
    }
    private fun searchCityWeather(cityName: String) {
        val url = "$url?q=$cityName&appid=$apiKey&units=imperial"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext,"Your city could not be found",Toast.LENGTH_LONG).show()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val json = JSONObject(body)
                val code = json.getInt("cod")

                if(code != 200) { //200 means request is successful
                    Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        applicationContext,
                        "Your city could not be found",
                        Toast.LENGTH_LONG
                    ).show()
                    }
                }
                else{
                    val main = json.getJSONObject("main")
                    val temp = main.getString("temp")
                    val sunriseTime = json.getJSONObject("sys").getLong("sunrise")
                    val sunsetTime = json.getJSONObject("sys").getLong("sunset")
                    val cityName = json.getString("name")
                    val description = json.getJSONArray("weather").getJSONObject(0).getString("description")
                    val feelsLike = main.getDouble("feels_like")
                    val tempMin = main.getDouble("temp_min")
                    val tempMax = main.getDouble("temp_max")
                    val icon = json.getJSONArray("weather").getJSONObject(0).getString("icon")
                    val timezone = json.getLong("timezone")
                    //Log.d("WeatherIcon", icon)

                    runOnUiThread {
                        tempTextView.text = "Temperature: ${temp}°F"
                        sunriseTextView.text = "Sunrise: ${formatTime(sunriseTime + timezone)}" //uses UTC time + timezone for accurate time in location
                        sunsetTextView.text = "Sunset: ${formatTime(sunsetTime + timezone)}"
                        cityNameTextView.text = cityName
                        descriptionTextView.text = "Description: $description"
                        feelslikeTextView.text = "Feels Like: ${feelsLike}°F"
                        tempminTextView.text = "Temp Min: ${tempMin}°F"
                        tempmaxTextView.text = "Temp Max: ${tempMax}°F"

                        var iconCheck = when (icon) {
                            "01n" -> R.drawable._01n
                            "02n" -> R.drawable._02n
                            "03n" -> R.drawable._03n
                            "04n" -> R.drawable._04n
                            "09n" -> R.drawable._09n
                            "10n" -> R.drawable._10n
                            "11n" -> R.drawable._11n
                            "13n" -> R.drawable._13n
                            "50n" -> R.drawable._50n

                            "01d" -> R.drawable._01d
                            "02d" -> R.drawable._02d
                            "03d" -> R.drawable._03d
                            "04d" -> R.drawable._04d
                            "09d" -> R.drawable._09d
                            "10d" -> R.drawable._10d
                            "11d" -> R.drawable._11d
                            "13d" -> R.drawable._13d
                            "50d" -> R.drawable._50d
                            else  -> null
                        }
                        iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, iconCheck!!))
                    }
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = Date(timestamp * 1000)
        return dateFormat.format(date)
    }

    /**prevents DetailedActivity from opening when city is not found */
    private fun favoriteCity(city_name:String){
        Log.d("addCity", "$city_name")
        if(getCities(this).contains(city_name)){
            Log.d("removeCity", "$city_name")
            removeCity(this,city_name)
            favoriteButton.text = getString(R.string.favoriteButton)
        }
        else{
            Log.d("addCity", "$city_name")
            addCity(this,city_name)
            favoriteButton.text = getString(R.string.unfavoriteButton)

        }
    }

}