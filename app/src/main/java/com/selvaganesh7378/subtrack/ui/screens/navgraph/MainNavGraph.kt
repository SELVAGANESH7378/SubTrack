package com.selvaganesh7378.subtrack.ui.screens.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.selvaganesh7378.subtrack.ui.screens.Screen
import com.selvaganesh7378.subtrack.ui.screens.profile.ProfileScreen
import com.selvaganesh7378.subtrack.ui.screens.subscription.addeditsubscription.AddEditSubscriptionScreen

@RequiresApi(Build.VERSION_CODES.O)
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
                onNavigateBack = { navController.navigateUp() },
                onNavigateToLogin = {}
            )
        }

        composable(
            route = Screen.Main.AddEditSubscription.route + "?subscriptionId={subscriptionId}",
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            AddEditSubscriptionScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }

}