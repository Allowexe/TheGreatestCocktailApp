package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.veith.thegreatestcocktailapp.DrinksActivity
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CategoriesScreen(modifier: Modifier) {
    val categoriesState = remember { mutableStateOf(emptyList<String>()) }
    val isLoading = remember { mutableStateOf(true) }
    val context = LocalContext.current

    val purpleAccent = Color(0xFF9D4EDD)
    val darkBackground = Color(0xFF0F0F0F)

    LaunchedEffect(Unit) {
        NetworkManager.api.getCategories().enqueue(object : Callback<Drinks> {
            override fun onResponse(call: Call<Drinks>, response: Response<Drinks>) {
                if (response.isSuccessful) {
                    val list = response.body()?.drinks?.map { it.category } ?: emptyList()
                    categoriesState.value = list
                }
                isLoading.value = false
            }
            override fun onFailure(call: Call<Drinks>, t: Throwable) {
                Log.e("API_ERROR", "Categories API call failed : ${t.message}")
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
        AnimatedContent(
            targetState = isLoading.value,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "LoadingTransition"
        ) { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = purpleAccent)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Categories",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    itemsIndexed(categoriesState.value) { index, category ->
                        var isItemVisible by remember { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            isItemVisible = true
                        }

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
                                    val intent = Intent(context, DrinksActivity::class.java)
                                    intent.putExtra("category", category)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.05f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    purpleAccent.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(purpleAccent, androidx.compose.foundation.shape.CircleShape)
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