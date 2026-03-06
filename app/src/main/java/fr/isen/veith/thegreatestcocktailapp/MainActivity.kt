package fr.isen.veith.thegreatestcocktailapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.veith.thegreatestcocktailapp.screens.CategoriesScreen
import fr.isen.veith.thegreatestcocktailapp.screens.DetailCocktailScreen
import fr.isen.veith.thegreatestcocktailapp.screens.FavoriteScreen
import fr.isen.veith.thegreatestcocktailapp.screens.SearchScreen

import fr.isen.veith.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme
import kotlinx.coroutines.launch


enum class NavigationItem(
    val titleID: Int,
    val icon: ImageVector,
    val route: String
) {
    Home(R.string.nav_title_random, Icons.Default.Home, "home"),
    List(R.string.nav_title_category, Icons.Default.Menu, "list"),
    Search(R.string.nav_title_search, Icons.Default.Search, "search"),
    Fav(R.string.nav_title_fav, Icons.Default.Favorite, "fav")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheGreatestCocktailAppTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                val startNavigationItem = NavigationItem.Home
                val currentNavigationItem = remember { mutableStateOf(startNavigationItem) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {

                        TopAppBar(
                            snackbarHostState = snackBarHostState,
                            titleResId = currentNavigationItem.value.titleID
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationItem.entries.forEach { navigationItem ->
                                NavigationBarItem(
                                    selected = currentNavigationItem.value == navigationItem,
                                    onClick = {
                                        navController.navigate(navigationItem.route) {

                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        currentNavigationItem.value = navigationItem
                                    },
                                    label = {
                                        Text(stringResource(navigationItem.titleID))
                                    },
                                    icon = {
                                        Icon(navigationItem.icon, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startNavigationItem.route
                    ) {
                        NavigationItem.entries.forEach { navigationItem ->
                            composable(navigationItem.route) {
                                when (navigationItem) {
                                    NavigationItem.Home -> DetailCocktailScreen(Modifier.padding(innerPadding))
                                    NavigationItem.List -> CategoriesScreen(Modifier.padding(innerPadding))
                                    NavigationItem.Search -> SearchScreen(Modifier.padding(innerPadding))
                                    NavigationItem.Fav -> FavoriteScreen(Modifier.padding(innerPadding))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    snackbarHostState: SnackbarHostState,
    titleResId: Int,
    drinkID: String? = null
) {
    CenterAlignedTopAppBar(
        title = {

            Text(stringResource(titleResId))
        },
        actions = {

            if (drinkID != null) {
                val added = stringResource(R.string.snackbar_added)
                val removed = stringResource(R.string.snackbar_removed)
                val snackbarScope = rememberCoroutineScope()
                val context = LocalContext.current
                val sharedPreferences = SharedPreferencesHelper(context)
                val drinkList = sharedPreferences.getFavoriteList()
                val isFav = remember { mutableStateOf(getFavoriteStatusForID(drinkID, drinkList)) }

                IconToggleButton(
                    checked = isFav.value,
                    onCheckedChange = {
                        isFav.value = !isFav.value
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(if (isFav.value) added else removed)
                        }
                        updateFavoriteList(
                            drinkID,
                            isFav.value,
                            sharedPreferences,
                            drinkList
                        )
                    }
                ) {
                    Icon(
                        imageVector = if (isFav.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "fav"
                    )
                }
            }
        }
    )
}

fun getFavoriteStatusForID(drinkID: String?, list: ArrayList<String>): Boolean {
    return list.contains(drinkID)
}

fun updateFavoriteList(
    drinkID: String,
    shouldBeAdded: Boolean,
    sharedPreferencesHelper: SharedPreferencesHelper,
    list: ArrayList<String>
) {
    if (shouldBeAdded) {
        if (!list.contains(drinkID)) list.add(drinkID)
    } else {
        list.remove(drinkID)
    }
    sharedPreferencesHelper.saveFavoriteList(list)
}