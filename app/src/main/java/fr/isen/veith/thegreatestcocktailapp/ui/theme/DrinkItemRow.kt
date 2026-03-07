package fr.isen.veith.thegreatestcocktailapp.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fr.isen.veith.thegreatestcocktailapp.network.DrinkModel

@Composable
fun DrinkItemRow(drink: DrinkModel, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = drink.imageURL,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(drink.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (drink.category.isNotEmpty()) {
                    Text(drink.category, color = Color.White.copy(0.6f), fontSize = 14.sp)
                }
            }
        }
    }
}