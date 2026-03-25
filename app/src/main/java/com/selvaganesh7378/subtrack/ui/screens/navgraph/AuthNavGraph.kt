package com.selvaganesh7378.subtrack.ui.screens.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.selvaganesh7378.subtrack.ui.screens.Screen
import com.selvaganesh7378.subtrack.ui.screens.login.LoginScreen
import com.selvaganesh7378.subtrack.ui.screens.register.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Screen.Auth.route,
        startDestination = Screen.Auth.Login.route
    ) {
        composable(Screen.Auth.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Auth.Register.route)
                }
            )
        }

        composable(Screen.Auth.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}