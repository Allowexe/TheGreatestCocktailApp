package fr.isen.veith.thegreatestcocktailapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fr.isen.veith.thegreatestcocktailapp.R
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Ingredient(val ingredient: String, val measure: String)

@Composable
fun DetailCocktailScreen(modifier: Modifier, drinkID: String? = null) {
    val ingredients = listOf(
        Ingredient("vodka", "4cl"),
        Ingredient("curacao", "3cl"),
        Ingredient("lime juice", "2cl")
    )

    val drink = remember { mutableStateOf(DrinkModel()) }

    LaunchedEffect(Unit) {
        val call = NetworkManager.api.getRandomCocktail()
        call.enqueue(object : Callback<Drinks> {
            override fun onResponse(p0: Call<Drinks?>,p1: Response<Drinks?>) {
                drink.value = p1.body()?.drinks?.first() ?: DrinkModel()
            }

            override fun onFailure(p0: Call<Drinks?>, p1: Throwable) {
                Log.e("error", p1.message.toString())
            }
        })
    }

    Column(modifier
        .fillMaxSize()
        .background(brush = Brush.linearGradient(listOf(Color.Cyan, Color.Black)))
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AsyncImage(
            drink.value.imageURL,
            contentDescription = drink.value.name,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

//        Image(
//            painterResource(R.drawable.cocktail),
//            contentDescription = "photo cocktail",
//            modifier = Modifier
//                .size(200.dp)
//                .clip(CircleShape),
//            contentScale = ContentScale.Crop
//            )

        Text(drink.value.name,
            color = Color.White,
            fontSize = 30.sp)

        Row(Modifier.wrapContentHeight()) {
            Box(Modifier
                .background(
                    color = Color.White.copy(0.7f),
                    shape = RoundedCornerShape(20.dp)
                )
            ) {
                Text(drink.value.category,
                    Modifier.padding(4.dp)
                )
            }
        }

        Text("glass type",
            color = Color.White,
            fontSize = 20.sp
        )

        Card(Modifier.fillMaxWidth(),
            RoundedCornerShape(25.dp),
            CardColors(Color.White.copy(0.2f),
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified)) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.detail_ingredients_title),
                    color = Color.White,
                    fontSize = 25.sp)

                ingredients.forEach { (ingredient, measure) ->
                    Row(Modifier.wrapContentHeight()) {
                        Text(ingredient, color = Color.White)
                        Spacer(Modifier.weight(1f))
                        Text(measure, color = Color.White)
                    }
                }
            }
        }
    }
}