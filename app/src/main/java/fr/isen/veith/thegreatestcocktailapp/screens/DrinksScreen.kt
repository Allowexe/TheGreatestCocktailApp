package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Menu
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


    val purpleAccent = Color(0xFF9D4EDD)
    val darkBackground = Color(0xFF0F0F0F)

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D004D), darkBackground)
                )
            )
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = purpleAccent
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = category,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(drinksState.value) { drink ->

                    Card(
                        onClick = {
                            val intent = Intent(context, DetailCocktailActivity::class.java)
                            intent.putExtra("drinkID", drink.id)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        border = BorderStroke(1.dp, purpleAccent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {

                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(1.dp, purpleAccent.copy(alpha = 0.5f)),
                                color = Color.Transparent
                            ) {
                                AsyncImage(
                                    model = drink.imageURL,
                                    contentDescription = drink.name,
                                    modifier = Modifier
                                        .size(65.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = drink.name,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )


                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Menu,
                                contentDescription = null,
                                tint = purpleAccent.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}