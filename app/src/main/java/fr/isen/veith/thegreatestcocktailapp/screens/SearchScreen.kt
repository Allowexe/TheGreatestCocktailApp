package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.veith.thegreatestcocktailapp.DetailCocktailActivity
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel
import fr.isen.veith.thegreatestcocktailapp.network.Drinks
import fr.isen.veith.thegreatestcocktailapp.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SearchScreen(modifier: Modifier) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateOf<List<DrinkModel>>(emptyList()) }
    val isSearching = remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Chercher un cocktail...") },
            modifier = Modifier.fillMaxWidth(),
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
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(searchResults.value) { drink ->

                    Button(
                        onClick = {
                            val intent = Intent(context, DetailCocktailActivity::class.java)
                            intent.putExtra("drinkID", drink.id)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(drink.name)
                    }
                }
            }
        }
    }
}