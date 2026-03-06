package fr.isen.veith.thegreatestcocktailapp.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.veith.thegreatestcocktailapp.DrinksActivity

@Composable
fun CategoriesScreen(modifier: Modifier) {
    val categories = listOf(
        "Beer",
        "Cocktail",
        "Cocoa",
        "Coffee / Tea",
        "Homemade Liqueur",
        "Ordinary Drink",
        "Other / Unknown",
        "Punch / Party Drink",
        "Shake",
        "Shot",
        "Soft drink"
    )

    LazyColumn(modifier
        .fillMaxSize()
        .background(brush = Brush.linearGradient(listOf(Color.Cyan, Color.Black)))
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(categories) { category ->
            val context = LocalContext.current
            Button(
                onClick = {
                    val intent = Intent(context, DrinksActivity::class.java)
                    intent.putExtra("category", category)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonColors(
                    Color.White.copy(0.3f),
                    Color.White,
                    Color.Unspecified,
                    Color.Unspecified
                )
            ) {
                Text(category, fontSize = 30.sp)
            }
        }
    }
}