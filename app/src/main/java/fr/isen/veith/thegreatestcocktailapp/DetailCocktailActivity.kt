package fr.isen.veith.thegreatestcocktailapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import fr.isen.veith.thegreatestcocktailapp.screens.DetailCocktailScreen
import fr.isen.veith.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme

class DetailCocktailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val drinkID = intent.getStringExtra("drinkID").toString()
        enableEdgeToEdge()
        setContent {
            TheGreatestCocktailAppTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                    TopAppBar(snackBarHostState, drinkID)
                }) { innerPadding ->
                    DetailCocktailScreen(Modifier.padding(innerPadding), drinkID)
                }
            }
        }
    }
}