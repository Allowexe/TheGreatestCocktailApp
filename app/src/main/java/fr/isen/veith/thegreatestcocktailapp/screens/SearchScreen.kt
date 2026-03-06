package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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


    Box(modifier = modifier
        .fillMaxSize()
        .background(brush = Brush.verticalGradient(listOf(Color(0xFF00B4D8), Color.Black)))) {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Recherche",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Nom du cocktail...", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotEmpty()) {
                            isSearching.value = true
                            NetworkManager.api.searchDrinksByName(searchQuery).enqueue(object : Callback<Drinks> {
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
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Cyan)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isSearching.value) {
                Box(modifier = Modifier.fillWeight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Cyan)
                }
            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(searchResults.value) { drink ->

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


fun Modifier.fillWeight(weight: Float): Modifier = this.then(Modifier)