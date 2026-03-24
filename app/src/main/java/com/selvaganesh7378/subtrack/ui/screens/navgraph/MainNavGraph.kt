package com.selvaganesh7378.subtrack.ui.screens.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.selvaganesh7378.subtrack.ui.screens.Screen
import com.selvaganesh7378.subtrack.ui.screens.profile.ProfileScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        route = Screen.Main.route,
        startDestination = "main_container"
    ) {

        composable("main_container") {
            MainScreen(rootNavController = navController)
        }

        composable(Screen.Main.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }

}