package com.selvaganesh7378.subtrack.ui.screens

sealed class Screen(val route: String) {

    // Root
    object Splash : Screen("splash")

    // Auth Graph
    object Auth : Screen("auth_graph") {
        object Login    : Screen("login")
        object Register : Screen("register")
    }

    // Main Graph
    object Main : Screen("main_graph") {
        object Subscription : Screen("subscription")
        object Calendar     : Screen("calendar")
        object Profile      : Screen("profile")

        object AddEditSubscription : Screen("Add Edit Subscription")
    }
}