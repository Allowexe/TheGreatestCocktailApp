package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import fr.isen.veith.thegreatestcocktailapp.DetailCocktailActivity
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import fr.isen.veith.thegreatestcocktailapp.ui.theme.DrinkItemRow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SearchScreen(modifier: Modifier) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateOf<List<DrinkModel>>(emptyList()) }
    val isSearching = remember { mutableStateOf(false) }
    var searchByIngredient by remember { mutableStateOf(false) }

    val purpleAccent = Color(0xFF9D4EDD)
    val darkBackground = Color(0xFF0F0F0F)

    Box(modifier = modifier
        .fillMaxSize()
        .background(brush = Brush.verticalGradient(listOf(Color(0xFF2D004D), darkBackground)))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Recherche",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (searchByIngredient) "Par Ingrédient" else "Par Nom",
                    color = purpleAccent.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = searchByIngredient,
                    onCheckedChange = {
                        searchByIngredient = it
                        searchResults.value = emptyList()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = purpleAccent,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Black
                    )
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (searchByIngredient) "Ex: Vodka, Rum..." else "Margarita, Mojito...",
                        color = Color.White.copy(alpha = 0.4f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = purpleAccent,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = purpleAccent
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotEmpty()) {
                            isSearching.value = true
                            val call = if (searchByIngredient) {
                                NetworkManager.api.filterByIngredient(searchQuery)
                            } else {
                                NetworkManager.api.searchDrinksByName(searchQuery)
                            }

                            call.enqueue(object : Callback<Drinks> {
                                override fun onResponse(call: Call<Drinks>, response: Response<Drinks>) {
                                    searchResults.value = response.body()?.drinks ?: emptyList()
                                    isSearching.value = false
                                }
                                override fun onFailure(call: Call<Drinks>, t: Throwable) {
                                    isSearching.value = false
                                }
                            })
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = purpleAccent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Zone de résultats animée
            AnimatedContent(
                targetState = isSearching.value,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "SearchState"
            ) { loading ->
                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = purpleAccent)
                    }
                } else {
                    if (searchResults.value.isEmpty() && searchQuery.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Aucun cocktail trouvé", color = Color.White.copy(alpha = 0.5f))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 90.dp)
                        ) {
                            itemsIndexed(searchResults.value) { index, drink ->
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
                                    DrinkItemRow(drink = drink) {
                                        val intent = Intent(context, DetailCocktailActivity::class.java)
                                        intent.putExtra("drinkID", drink.id)
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}