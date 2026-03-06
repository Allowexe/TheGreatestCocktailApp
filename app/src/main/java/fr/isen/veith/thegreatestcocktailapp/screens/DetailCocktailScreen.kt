package fr.isen.veith.thegreatestcocktailapp.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun DetailCocktailScreen(modifier: Modifier, drinkID: String? = null) {
    val drinkState = remember { mutableStateOf<DrinkModel?>(null) }
    val scrollState = rememberScrollState()


    LaunchedEffect(drinkID) {
        val call = if (drinkID != null) {
            NetworkManager.api.getDrinkById(drinkID)
        } else {
            NetworkManager.api.getRandomCocktail()
        }

        call.enqueue(object : Callback<Drinks> {
            override fun onResponse(call: Call<Drinks>, response: Response<Drinks>) {
                drinkState.value = response.body()?.drinks?.firstOrNull()
            }
            override fun onFailure(call: Call<Drinks>, t: Throwable) {
                Log.e("API_ERROR", "Échec de récupération : ${t.message}")
            }
        })
    }

    val drink = drinkState.value


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(Color(0xFF00B4D8), Color.Black)))
    ) {
        if (drink == null) {

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                AsyncImage(
                    model = drink.imageURL,
                    contentDescription = drink.name,
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(0.3f)),
                    contentScale = ContentScale.Crop
                )


                Text(
                    text = drink.name,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )


                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryBadge(text = drink.category, color = Color(0xFF4895EF))
                    drink.glass?.let { CategoryBadge(text = it, color = Color(0xFF4CC9F0)) }
                }


                DetailCard(title = stringResource(R.string.detail_ingredients_title)) {
                    val list = drink.getIngredientsList()
                    if (list.isEmpty()) {
                        Text("Aucun ingrédient répertorié", color = Color.White.copy(0.7f))
                    } else {
                        list.forEach { item ->
                            Text("• $item", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }


                DetailCard(title = "Recette") {
                    Text(
                        text = drink.instructions ?: "Aucune instruction disponible.",
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }


                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun InfoBadge(text: String) {
    Surface(
        color = Color.White.copy(0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = Color.Cyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
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
            fontWeight = FontWeight.Medium
        )
    }
}