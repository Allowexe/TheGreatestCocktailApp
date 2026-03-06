package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
                Log.e("API_ERROR", "Erreur catégories : ${t.message}")
                isLoading.value = false
            }
        })
    }


    Box(modifier = modifier
        .fillMaxSize()
        .background(brush = Brush.verticalGradient(listOf(Color.Cyan, Color.Black)))) {

        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categoriesState.value) { category ->
                    Button(
                        onClick = {
                            val intent = Intent(context, DrinksActivity::class.java)
                            intent.putExtra("category", category)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(0.3f),
                            contentColor = Color.White
                        )
                    ) {
                        Text(category, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}