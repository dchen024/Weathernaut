package com.example.weathernaut

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val url = "https://api.openweathermap.org/data/2.5/weather"
    private val apiKey = "13e14e0c8b68f04c958f100877a0a805"

    private lateinit var searchBarEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var tempTextView: TextView
    private lateinit var sunsetTextView: TextView
    private lateinit var sunriseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBarEditText = findViewById(R.id.searchBarEditText)
        searchButton = findViewById(R.id.searchButton)
        tempTextView = findViewById(R.id.tempTextView)
        sunsetTextView = findViewById(R.id.sunsetTextView)
        sunriseTextView = findViewById(R.id.sunriseTextView)

        searchButton.setOnClickListener {
            val cityName = searchBarEditText.text.toString()
            searchCityWeather(cityName)
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
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val json = JSONObject(body)
                val main = json.getJSONObject("main")
                val temp = main.getString("temp")
                val sunriseTime = json.getJSONObject("sys").getString("sunrise").toLong()
                val sunsetTime = json.getJSONObject("sys").getString("sunset").toLong()

                runOnUiThread {
                    tempTextView.text = "Temperature: ${temp}°F"
                    sunriseTextView.text = "Sunrise: ${formatTime(sunriseTime)}"
                    sunsetTextView.text = "Sunset: ${formatTime(sunsetTime)}"
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