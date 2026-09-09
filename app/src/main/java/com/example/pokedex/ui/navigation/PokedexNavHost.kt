package com.example.pokedex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.ui.detail.DetailScreen
import com.example.pokedex.ui.home.HomeScreen
import com.example.pokedex.ui.welcome.WelcomeScreen

const val ARG_POKEMON_ID = "pokemonId"

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val DETAIL = "detail/{$ARG_POKEMON_ID}"
    fun detail(id: Int) = "detail/$id"
}

@Composable
fun PokedexNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onTimeout = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onPokemonClick = { id -> navController.navigate(Routes.detail(id)) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(ARG_POKEMON_ID) { type = NavType.IntType })
        ) {
            DetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
