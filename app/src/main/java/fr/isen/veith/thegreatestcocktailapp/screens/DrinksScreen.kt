package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import android.util.Log
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
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DrinksScreen(modifier: Modifier, category: String) {
    val drinksState = remember { mutableStateOf<List<DrinkModel>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val context = LocalContext.current


    LaunchedEffect(category) {
        val call = NetworkManager.api.getDrinksByCategory(category)
        call.enqueue(object : Callback<Drinks> {
            override fun onResponse(call: Call<Drinks>, response: Response<Drinks>) {
                if (response.isSuccessful) {
                    drinksState.value = response.body()?.drinks ?: emptyList()
                }
                isLoading.value = false
            }

            override fun onFailure(call: Call<Drinks>, t: Throwable) {
                Log.e("API_ERROR", "Erreur cocktails : ${t.message}")
                isLoading.value = false
            }
        })
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(brush = Brush.verticalGradient(listOf(Color.Cyan, Color.Black)))) {

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Text(
                        text = category,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }


                items(drinksState.value) { drink ->
                    Button(
                        onClick = {

                            val intent = Intent(context, DetailCocktailActivity::class.java)
                            intent.putExtra("drinkID", drink.id)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(0.2f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            AsyncImage(
                                model = drink.imageURL,
                                contentDescription = drink.name,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(0.3f)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = drink.name,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}