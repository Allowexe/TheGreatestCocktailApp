package fr.isen.veith.thegreatestcocktailapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.veith.thegreatestcocktailapp.screens.CategoriesScreen
import fr.isen.veith.thegreatestcocktailapp.screens.DetailCocktailScreen
import fr.isen.veith.thegreatestcocktailapp.screens.FavoriteScreen
import fr.isen.veith.thegreatestcocktailapp.screens.SearchScreen
import fr.isen.veith.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme
import kotlinx.coroutines.launch

val PurpleAccent = Color(0xFF9D4EDD)
val DarkBackground = Color(0xFF0F0F0F)

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
                val context = LocalContext.current
                val snackBarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                val startNavigationItem = NavigationItem.Home
                val currentNavigationItem = remember { mutableStateOf(startNavigationItem) }


                val currentDrinkId = remember { mutableStateOf<String?>(null) }
                val currentDrinkName = remember { mutableStateOf("") }
                val currentDrinkInstructions = remember { mutableStateOf("") }
                val refreshTrigger = remember { mutableStateOf(0) }


                val onShareAction = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Découvre ce cocktail : ${currentDrinkName.value} !\n\nRecette :\n${currentDrinkInstructions.value}")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBackground,
                    topBar = {
                        TopAppBar(
                            snackbarHostState = snackBarHostState,
                            titleResId = currentNavigationItem.value.titleID,
                            drinkID = if (currentNavigationItem.value == NavigationItem.Home) currentDrinkId.value else null,
                            onReload = if (currentNavigationItem.value == NavigationItem.Home) {
                                { refreshTrigger.value++ }
                            } else null,

                            onShare = if (currentNavigationItem.value == NavigationItem.Home && currentDrinkId.value != null) {
                                onShareAction
                            } else null
                        )
                    },
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                    bottomBar = {
                        NavigationBar(containerColor = DarkBackground, tonalElevation = 8.dp) {
                            NavigationItem.entries.forEach { navigationItem ->
                                NavigationBarItem(
                                    selected = currentNavigationItem.value == navigationItem,
                                    onClick = {
                                        navController.navigate(navigationItem.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        currentNavigationItem.value = navigationItem
                                        if (navigationItem != NavigationItem.Home) currentDrinkId.value = null
                                    },
                                    label = { Text(stringResource(navigationItem.titleID)) },
                                    icon = { Icon(navigationItem.icon, contentDescription = null) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PurpleAccent,
                                        selectedTextColor = PurpleAccent,
                                        indicatorColor = PurpleAccent.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = startNavigationItem.route) {
                        composable(NavigationItem.Home.route) {
                            DetailCocktailScreen(
                                modifier = Modifier.padding(innerPadding),
                                refreshTrigger = refreshTrigger.value,

                                onDrinkLoaded = { id, name, instructions ->
                                    currentDrinkId.value = id
                                    currentDrinkName.value = name
                                    currentDrinkInstructions.value = instructions
                                }
                            )
                        }
                        composable(NavigationItem.List.route) { CategoriesScreen(Modifier.padding(innerPadding)) }
                        composable(NavigationItem.Search.route) { SearchScreen(Modifier.padding(innerPadding)) }
                        composable(NavigationItem.Fav.route) { FavoriteScreen(Modifier.padding(innerPadding)) }
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
    drinkID: String? = null,
    onReload: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(titleResId), color = Color.White, style = MaterialTheme.typography.titleLarge)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = Color.White
        ),
        actions = {
            if (onReload != null) {
                IconButton(onClick = onReload) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = Color.White)
                }
            }

            if (onShare != null) {
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
            }

            if (drinkID != null) {
                val added = stringResource(R.string.snackbar_added)
                val removed = stringResource(R.string.snackbar_removed)
                val snackbarScope = rememberCoroutineScope()
                val context = LocalContext.current
                val sharedPreferences = SharedPreferencesHelper(context)
                val drinkList = sharedPreferences.getFavoriteList()
                val isFav = remember(drinkID) { mutableStateOf(getFavoriteStatusForID(drinkID, drinkList)) }

                IconToggleButton(
                    checked = isFav.value,
                    onCheckedChange = {
                        isFav.value = !isFav.value
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(if (isFav.value) added else removed)
                        }
                        updateFavoriteList(drinkID, isFav.value, sharedPreferences, drinkList)
                    }
                ) {
                    Icon(
                        imageVector = if (isFav.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "fav",
                        tint = if (isFav.value) PurpleAccent else Color.White
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