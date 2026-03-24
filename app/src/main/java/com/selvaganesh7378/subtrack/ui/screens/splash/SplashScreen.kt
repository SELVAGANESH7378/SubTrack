package com.selvaganesh7378.subtrack.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.selvaganesh7378.subtrack.ui.screens.Screen
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Replace this with your actual auth check (Firebase, DataStore, etc.)
    val isLoggedIn = remember {
        // e.g., FirebaseAuth.getInstance().currentUser != null
        // or SharedPreferences check
        false
    }

    LaunchedEffect(Unit) {
        delay(1500L)
        if (isLoggedIn) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("MyApp", style = MaterialTheme.typography.headlineLarge)
    }
}
