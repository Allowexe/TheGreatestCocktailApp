package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

    val purpleAccent = Color(0xFF9D4EDD)
    val darkBackground = Color(0xFF0F0F0F)

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D004D), darkBackground)
                )
            )
    ) {
        AnimatedContent(
            targetState = isLoading.value,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "FavoriteState"
        ) { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = purpleAccent)
                }
            } else if (favoriteDrinks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No favorites for now",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "My Favorites",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    itemsIndexed(favoriteDrinks) { index, drink ->
                        var isItemVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { isItemVisible = true }

                        AnimatedVisibility(
                            visible = isItemVisible,
                            enter = fadeIn(animationSpec = tween(500, delayMillis = index * 40)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 3 },
                                        animationSpec = tween(500, delayMillis = index * 40)
                                    )
                        ) {
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
                                                .size(60.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Text(
                                        text = drink.name,
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }
}