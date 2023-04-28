package com.example.weathernaut

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

        /*
        TODO:
        3) Move Favorite and Remove Buttons to DetailedActivity.kt
        4) Add toast in DetailedActivity.kt when city name is spelled wrong
         */

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

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

        searchCityWeather(city_name)
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
                val code = json.getString("cod")

                if(code != "200") {
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
                    val sunriseTime = json.getJSONObject("sys").getString("sunrise").toLong()
                    val sunsetTime = json.getJSONObject("sys").getString("sunset").toLong()
                    val cityname = json.getString("name")
                    val description = json.getJSONArray("weather").getJSONObject(0).getString("description")
                    val feelslike = main.getString("feels_like")
                    val tempmin = main.getString("temp_min")
                    val tempmax = main.getString("temp_max")
                    val icon = json.getJSONArray("weather").getJSONObject(0).getString("icon")
                    Log.d("WeatherIcon", icon)


                    runOnUiThread {
                        tempTextView.text = "Temperature: ${temp}°F"
                        sunriseTextView.text = "Sunrise: ${formatTime(sunriseTime)}"
                        sunsetTextView.text = "Sunset: ${formatTime(sunsetTime)}"
                        cityNameTextView.text = "${cityname}"
                        descriptionTextView.text = "Description: ${description}"
                        feelslikeTextView.text = "Feels Like: ${feelslike}°F"
                        tempminTextView.text = "Temp Min: ${tempmin}°F"
                        tempmaxTextView.text = "Temp Max: ${tempmax}°F"

                        when (icon) {
                            "01n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._01d))
                            "02n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._02d))
                            "03n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._03d))
                            "04n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._04d))
                            "09n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._09d))
                            "10n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._10d))
                            "11n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._11d))
                            "13n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._13d))
                            "50n" -> iconImageView.setImageDrawable(AppCompatResources.getDrawable(this@DetailedActivity, R.drawable._50d))
                        }
                    }
                }
            }

        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val date = Date(timestamp * 1000)
        return dateFormat.format(date)
    }
}