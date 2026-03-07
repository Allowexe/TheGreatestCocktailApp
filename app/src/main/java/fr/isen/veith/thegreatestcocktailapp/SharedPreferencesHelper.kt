package fr.isen.veith.thegreatestcocktailapp

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val key = "favDrinks"
    private val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveFavoriteList(list: ArrayList<String>) {
        val json = gson.toJson(list)
        sharedPreferences.edit { putString(key, json) }
    }

    fun getFavoriteList(): ArrayList<String> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}