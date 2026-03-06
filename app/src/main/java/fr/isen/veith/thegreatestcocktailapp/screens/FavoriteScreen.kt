package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fr.isen.veith.thegreatestcocktailapp.DetailCocktailActivity
import fr.isen.veith.thegreatestcocktailapp.SharedPreferencesHelper
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun FavoriteScreen(modifier: Modifier) {
    val context = LocalContext.current
    val sharedPreferences = remember { SharedPreferencesHelper(context) }


    val favoriteDrinks = remember { mutableStateListOf<DrinkModel>() }
    val isLoading = remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        val favIds = sharedPreferences.getFavoriteList()
        favoriteDrinks.clear()

        if (favIds.isEmpty()) {
            isLoading.value = false
        } else {
            var count = 0
            favIds.forEach { id ->
                NetworkManager.api.getDrinkById(id).enqueue(object : Callback<Drinks> {
                    override fun onResponse(call: Call<Drinks>, response: Response<Drinks>) {
                        response.body()?.drinks?.firstOrNull()?.let {
                            favoriteDrinks.add(it)
                        }
                        count++
                        if (count == favIds.size) isLoading.value = false
                    }
                    override fun onFailure(call: Call<Drinks>, t: Throwable) {
                        count++
                        if (count == favIds.size) isLoading.value = false
                    }
                })
            }
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(brush = Brush.verticalGradient(listOf(Color.Cyan, Color.Black)))) {

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else if (favoriteDrinks.isEmpty()) {
            Text(
                "Aucun favori pour le moment",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteDrinks) { drink ->
                    Button(
                        onClick = {
                            val intent = Intent(context, DetailCocktailActivity::class.java)
                            intent.putExtra("drinkID", drink.id)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(0.2f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            AsyncImage(
                                model = drink.imageURL,
                                contentDescription = drink.name,
                                modifier = Modifier.size(50.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = drink.name,
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}