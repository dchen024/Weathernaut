package com.example.weathernaut

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapirecyclerview.WeatherAdapter
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

data class WeatherData(val Cityname: String, val temp: String, val sunrise: Long, val sunset: Long)
class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val url = "https://api.openweathermap.org/data/2.5/weather"
    private val apiKey = "13e14e0c8b68f04c958f100877a0a805"

    private lateinit var searchBarEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var weatherList: MutableList<WeatherData>
    private lateinit var rvweather: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBarEditText = findViewById(R.id.searchBarEditText)
        searchButton = findViewById(R.id.searchButton)
        rvweather = findViewById(R.id.weather_list)

        weatherList = mutableListOf()

        val cities = listOf("New York", "Los Angeles", "Houston", "Phoenix", "San Antonio") // Replace with actual city names
        for (city in cities) {
            searchCityWeather(city)
        }

        searchButton.setOnClickListener {
            val cityName = URLEncoder.encode(searchBarEditText.text.toString(),"UTF-8")

            if(isCityFound(cityName) == false){
                Toast.makeText(applicationContext,"Your city could not be found",Toast.LENGTH_LONG).show()
            }
            else if(isCityFound(cityName) == true){
                callDetailedActivity()
            }

            Log.d("URL","$url?q=$cityName&appid=$apiKey&units=imperial")
        }

    }

    private fun callDetailedActivity() { //Used to pass city name to DetailedActivity.kt
        val editText = findViewById<EditText>(R.id.searchBarEditText)
        val message = editText.text.toString()

        val intent = Intent(this,DetailedActivity::class.java).also{
            it.putExtra("CITY_NAME", message)
            startActivity(it)
        }
    }

    private var cityCount = 0 // Add this variable to keep track of city count
                                // We can also just do the size of the mutablelist to find number of cities. -Daniel
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
                val main = json.getJSONObject("main")

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
                else {
                    val temp = main.getString("temp")
                    val sunriseTime = json.getJSONObject("sys").getString("sunrise").toLong()
                    val sunsetTime = json.getJSONObject("sys").getString("sunset").toLong()

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
            }

        })
    }

    private fun isCityFound(cityName: String): Boolean { //Used to prevent going to DetailedActivity when city is not found
        val url = "$url?q=$cityName&appid=$apiKey&units=imperial"
        val request = Request.Builder()
            .url(url)
            .build()

        val future = CompletableFuture<Boolean>()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext,"Your city could not be found",Toast.LENGTH_LONG).show()
                future.complete(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val json = JSONObject(body)
                val code = json.getString("cod")
                if (code != "200") {
                    future.complete(false)
                } else {
                    future.complete(true)
                }
            }
        })

        return future.get()
    }


    @SuppressLint("SimpleDateFormat")
    private fun formatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val date = Date(timestamp * 1000)
        return dateFormat.format(date)
    }
}
