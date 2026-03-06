package fr.isen.veith.thegreatestcocktailapp.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Drinks(
    val drinks: List<DrinkModel>?
): Serializable

class DrinkModel(
    @SerializedName("idDrink") val id: String = "",
    @SerializedName("strDrink") val name: String = "",
    @SerializedName("strCategory") val category: String = "",
    @SerializedName("strDrinkThumb") val imageURL: String = "",
    @SerializedName("strInstructions") val instructions: String? = "",
    @SerializedName("strGlass") val glass: String? = "",

    @SerializedName("strIngredient1") val ingredient1: String? = null,
    @SerializedName("strIngredient2") val ingredient2: String? = null,
    @SerializedName("strIngredient3") val ingredient3: String? = null,
    @SerializedName("strIngredient4") val ingredient4: String? = null,
    @SerializedName("strIngredient5") val ingredient5: String? = null,

    @SerializedName("strMeasure1") val measure1: String? = null,
    @SerializedName("strMeasure2") val measure2: String? = null,
    @SerializedName("strMeasure3") val measure3: String? = null,
    @SerializedName("strMeasure4") val measure4: String? = null,
    @SerializedName("strMeasure5") val measure5: String? = null,

): Serializable {
    fun getIngredientsList(): List<String> {
        return listOfNotNull(
            if (!ingredient1.isNullOrBlank()) "$measure1 $ingredient1".trim() else null,
            if (!ingredient2.isNullOrBlank()) "$measure2 $ingredient2".trim() else null,
            if (!ingredient3.isNullOrBlank()) "$measure3 $ingredient3".trim() else null,
            if (!ingredient4.isNullOrBlank()) "$measure4 $ingredient4".trim() else null,
            if (!ingredient5.isNullOrBlank()) "$measure5 $ingredient5".trim() else null
        )
    }
}