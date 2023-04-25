package com.example.weathernaut

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapirecyclerview.WeatherAdapter
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class WeatherData(val Cityname: String, val temp: String, val sunrise: Long, val sunset: Long)
class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val url = "https://api.openweathermap.org/data/2.5/weather"
    private val apiKey = "13e14e0c8b68f04c958f100877a0a805"

    private lateinit var searchBarEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var tempTextView: TextView
    private lateinit var sunsetTextView: TextView
    private lateinit var sunriseTextView: TextView
    private lateinit var favoriteButton : Button
    private lateinit var removeButton : Button
    private lateinit var weatherList: MutableList<WeatherData>
    private lateinit var rvweather: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBarEditText = findViewById(R.id.searchBarEditText)
        searchButton = findViewById(R.id.searchButton)
        tempTextView = findViewById(R.id.tempTextView)
        sunsetTextView = findViewById(R.id.sunsetTextView)
        sunriseTextView = findViewById(R.id.sunriseTextView)
        rvweather = findViewById(R.id.weather_list)
        favoriteButton = findViewById(R.id.favoriteButton)
        removeButton = findViewById(R.id.removeButton)
        weatherList = mutableListOf()

        val cities = listOf("New York", "Los Angeles", "Houston", "Phoenix", "San Antonio") // Replace with actual city names
        for (city in cities) {
            searchCityWeather(city)
        }

        searchButton.setOnClickListener {
            val cityName = searchBarEditText.text.toString()
            searchCityWeather(cityName)
        }

        //Add city to recycler view
        favoriteButton.setOnClickListener {
            val cityName = searchBarEditText.text.toString()
            val url = "$url?q=$cityName&appid=$apiKey&units=imperial"
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val json = JSONObject(body)
                    val main = json.getJSONObject("main")
                    val temp = main.getString("temp")
                    val sunriseTime = json.getJSONObject("sys").getString("sunrise").toLong()
                    val sunsetTime = json.getJSONObject("sys").getString("sunset").toLong()

                    val weatherData = WeatherData(cityName, temp, sunriseTime, sunsetTime)
                    weatherList.add(weatherData)

                    runOnUiThread {
                        val adapter = WeatherAdapter(weatherList)
                        rvweather.adapter = adapter
                        rvweather.layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }
            })
        }
        //Remove City from recycler view
        removeButton.setOnClickListener {
            val cityToRemove = searchBarEditText.text.toString()
            for (i in weatherList.indices) {
                if (weatherList[i].Cityname == cityToRemove) {
                    weatherList.removeAt(i)
                    break
                }
            }
            val adapter = WeatherAdapter(weatherList)
            rvweather.adapter = adapter
            rvweather.layoutManager = LinearLayoutManager(this@MainActivity)
        }

    }
    private var cityCount = 0 // Add this variable to keep track of city count
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

                val weatherData = WeatherData(cityName, temp, sunriseTime, sunsetTime)
                if (cityCount < 5) {
                    weatherList.add(weatherData)
                    cityCount++
                }
                if (cityCount == 5) {
                    runOnUiThread {
                        val adapter = WeatherAdapter(weatherList)
                        rvweather.adapter = adapter
                        rvweather.layoutManager = LinearLayoutManager(this@MainActivity)
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
