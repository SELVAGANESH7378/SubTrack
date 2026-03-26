package com.selvaganesh7378.subtrack.ui.screens.navgraph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.selvaganesh7378.subtrack.ui.screens.Screen
import com.selvaganesh7378.subtrack.ui.screens.splash.SplashScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }
            authNavGraph(navController)
            mainNavGraph(navController)
        }
    }

}