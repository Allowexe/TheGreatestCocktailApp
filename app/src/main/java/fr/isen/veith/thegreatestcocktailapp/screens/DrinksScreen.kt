package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.veith.thegreatestcocktailapp.DetailCocktailActivity
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DrinksScreen(modifier: Modifier, category: String) {
//    val drinks = listOf(
//        "Mojito",
//        "Negroni",
//        "Blue Lagoon",
//        "Cuba Libre"
//    )

    val drinks = remember { mutableStateOf<List<DrinkModel>>(listOf()) }

    LaunchedEffect(Unit) {
        val call = NetworkManager.api.getDrinksByCategory(category.replace(" ", "_"))
        call.enqueue(object : Callback<Drinks> {
            override fun onResponse(p0: Call<Drinks?>,p1: Response<Drinks?>) {
                drinks.value = p1.body()?.drinks ?: listOf()
            }

            override fun onFailure(p0: Call<Drinks?>, p1: Throwable) {
                Log.e("error", p1.message.toString())
            }
        })
    }

    LazyColumn(modifier
        .fillMaxSize()
        .background(brush = Brush.linearGradient(listOf(Color.Cyan, Color.Black)))
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(category, color = Color.White, fontSize = 50.sp)
        }

        items(drinks.value) { drink ->
            val context = LocalContext.current
            Button(
                onClick = {
                    val intent = Intent(context, DetailCocktailActivity::class.java)
                    intent.putExtra("drinkID", drink.id)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonColors(
                    Color.White.copy(0.3f),
                    Color.White,
                    Color.Unspecified,
                    Color.Unspecified
                )
            ) {
                Text(drink.name, fontSize = 30.sp)
            }
        }
    }
}