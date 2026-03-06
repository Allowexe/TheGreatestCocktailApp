package fr.isen.veith.thegreatestcocktailapp.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Drinks(
    val drinks: List<DrinkModel>
): Serializable

class DrinkModel(
    @SerializedName("idDrink") val id: String = "",
    @SerializedName("strDrink") val name: String = "",
    @SerializedName("strCategory") val category: String = "",
    @SerializedName("strDrinkThumb") val imageURL: String = ""
): Serializable