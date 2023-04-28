package com.example.weathernaut
import android.content.Context
import android.util.Log

class MyPreferences {
    companion object {
        val cities = setOf("New York", "London", "Tokyo", "Beijing", "Dubai", "Moscow")

        fun saveCities(context: Context, cities: Set<String>) {
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            prefs.edit().putStringSet("Cities", cities).apply()
        }

        fun getCities(context: Context): Set<String> {
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            return prefs.getStringSet("Cities", cities) ?: cities
        }

        fun removeCity(context: Context, city: String) {
            val cities = getCities(context).toMutableSet()
            cities.remove(city)
            saveCities(context, cities)
            Log.d("removeCity", "$cities")
        }

        fun addCity(context: Context, city: String) {
            val cities = getCities(context).toMutableSet()
            cities.add(city)
            saveCities(context, cities)
            Log.d("addCity", "$cities")
        }
    }
}


