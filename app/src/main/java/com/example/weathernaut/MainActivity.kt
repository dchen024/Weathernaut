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

data class WeatherData(val cityName: String, val temp: Double, val iconCheck:Int)
class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()                                 //Used for API
    private val url = "https://api.openweathermap.org/data/2.5/weather" //Used for API
    private val apiKey = "13e14e0c8b68f04c958f100877a0a805"             //Used for API

    private lateinit var searchBarEditText: EditText
    private lateinit var searchButton: Button

    private lateinit var weatherList: MutableList<WeatherData> //RecyclerView
    private lateinit var rvweather: RecyclerView               //RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBarEditText = findViewById(R.id.searchBarEditText)
        searchButton = findViewById(R.id.searchButton)

        rvweather = findViewById(R.id.weather_list)                     //RecyclerView
        weatherList = mutableListOf()                                   //RecyclerView

        /**Retrieves cities from persistent state in [MyPreferences]*/
        val savedCities = MyPreferences.getCities(this)

        for (city in savedCities) {                                 //RecyclerView
            val cityName = URLEncoder.encode(city,"UTF-8")
            searchCityWeather(cityName)
        }

        searchButton.setOnClickListener {
            //formats text to be URL compatible
            val cityName = URLEncoder.encode(searchBarEditText.text.toString(),"UTF-8")

            if(isCityFound(cityName)){ //prevents DetailedActivity from opening when city is not found
                callDetailedActivity()
            }
            else{
                Toast.makeText(applicationContext,"Your city could not be found",Toast.LENGTH_LONG).show()
            }
            //Log.d("URL","$url?q=$cityName&appid=$apiKey&units=imperial")
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

                val code = json.getInt("cod")
                val nameOfCity = json.getString("name")
                val icon = json.getJSONArray("weather").getJSONObject(0).getString("icon")

                if(code != 200) { //200 means request is successful
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            applicationContext,
                            "Your city could not be found",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else {
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

                    val temp = main.getDouble("temp")           //RecyclerView
                    Log.d("icon", icon)
                    val weatherData = WeatherData(nameOfCity,temp,iconCheck!!)     //RecyclerView

                    weatherList.add(weatherData) //RecyclerView

                    runOnUiThread { //RecyclerView
                        val adapter = WeatherAdapter(weatherList)
                        rvweather.adapter = adapter
                        rvweather.layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }
            }
        })
    }

    private fun isCityFound(cityName: String): Boolean { //Used to prevent going to DetailedActivity when city not found
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
    //Update RecyclerView if city was added or removed
    override fun onRestart() {
        super.onRestart()
        setContentView(R.layout.activity_main)

        searchBarEditText = findViewById(R.id.searchBarEditText)
        searchButton = findViewById(R.id.searchButton)

        rvweather = findViewById(R.id.weather_list)                     //RecyclerView
        weatherList = mutableListOf()                                   //RecyclerView

        /**Retrieves cities from persistent state in [MyPreferences]*/
        val savedCities = MyPreferences.getCities(this)

        for (city in savedCities) {                                 //RecyclerView
            val cityName = URLEncoder.encode(city,"UTF-8")
            searchCityWeather(cityName)
        }

        searchButton.setOnClickListener {
            //formats text to be URL compatible
            val cityName = URLEncoder.encode(searchBarEditText.text.toString(),"UTF-8")

            if(isCityFound(cityName)){ //prevents DetailedActivity from opening when city is not found
                callDetailedActivity()
            }
            else{
                Toast.makeText(applicationContext,"Your city could not be found",Toast.LENGTH_LONG).show()
            }
        }
    }

}
