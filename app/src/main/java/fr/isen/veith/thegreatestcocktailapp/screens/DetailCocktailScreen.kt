package fr.isen.veith.thegreatestcocktailapp.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val PurpleAccent = Color(0xFF9D4EDD)
val DeepPurple = Color(0xFF2D004D)
val DarkBackground = Color(0xFF0F0F0F)

@Composable
fun DetailCocktailScreen(
    modifier: Modifier,
    drinkID: String? = null,
    refreshTrigger: Int = 0,
    onDrinkLoaded: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val drinkState = remember { mutableStateOf<DrinkModel?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(drinkID, refreshTrigger) {
        if (drinkID == null) drinkState.value = null

        val call = if (drinkID != null) NetworkManager.api.getDrinkById(drinkID)
        else NetworkManager.api.getRandomCocktail()

        call.enqueue(object : Callback<Drinks> {
            override fun onResponse(call: Call<Drinks>, response: Response<Drinks>) {
                val drink = response.body()?.drinks?.firstOrNull()
                drinkState.value = drink
                drink?.let { onDrinkLoaded(it.id, it.name, it.instructions ?: "") }
            }
            override fun onFailure(call: Call<Drinks>, t: Throwable) {
                Log.e("API_ERROR", t.message.toString())
            }
        })
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(DeepPurple, DarkBackground)))
    ) {
        AnimatedContent(
            targetState = drinkState.value,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "DetailTransition"
        ) { drink ->
            if (drink == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PurpleAccent)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                        AsyncImage(
                            model = drink.imageURL,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().blur(25.dp).alpha(0.3f),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(4.dp, Color.White.copy(alpha = 0.2f)),
                                shadowElevation = 16.dp,
                                color = Color.Transparent
                            ) {
                                AsyncImage(
                                    model = drink.imageURL,
                                    contentDescription = drink.name,
                                    modifier = Modifier.size(200.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = drink.name,
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadge(drink.category, PurpleAccent)
                        drink.glass?.let {
                            Spacer(Modifier.width(8.dp))
                            CategoryBadge(it, PurpleAccent.copy(alpha = 0.7f))
                        }
                    }

                    Spacer(Modifier.height(24.dp))


                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(600, 200)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        GlassCard(title = "Ingrédients") {
                            drink.getIngredientsList().forEach { ingredient ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Box(Modifier.size(6.dp).background(PurpleAccent, CircleShape))
                                    Spacer(Modifier.width(12.dp))
                                    Text(ingredient, color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(600, 400)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        GlassCard(title = "Instructions") {
                            Text(
                                text = drink.instructions ?: "Aucune instruction.",
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 24.sp,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
fun GlassCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, color = PurpleAccent, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun CategoryBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}