package com.selvaganesh7378.subtrack.ui.screens.navgraph

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.selvaganesh7378.subtrack.R
import com.selvaganesh7378.subtrack.ui.screens.Screen
import com.selvaganesh7378.subtrack.ui.screens.calander.CalendarScreen
import com.selvaganesh7378.subtrack.ui.screens.subscription.SubscriptionScreen

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
) {
    object Subscription : BottomNavItem(
        Screen.Main.Subscription.route,
        "Subscription", R.drawable.subscriptions
    )

    object Calendar : BottomNavItem(
        Screen.Main.Calendar.route,
        "Calendar", R.drawable.calendar
    )
}

val bottomNavItems = listOf(BottomNavItem.Subscription, BottomNavItem.Calendar)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController,
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTitle = currentRoute?.replaceFirstChar { it.uppercase() } ?: "MyApp"

    // State to handle the dropdown menu visibility
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect {
            rootNavController.navigate(Screen.Auth.route) {
                popUpTo(rootNavController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(currentTitle) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        Box {
                            IconButton(
                                onClick = { expanded = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = "Profile Options"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Profile") },
                                    onClick = {
                                        expanded = false
                                        rootNavController.navigate(Screen.Main.Profile.route)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = {
                                        expanded = false
                                        viewModel.logout()
                                    }
                                )
                            }
                        }
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            innerNavController.navigate(item.route) {
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Main.Subscription.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Screen.Main.Subscription.route) {
                SubscriptionScreen()
            }
            composable(Screen.Main.Calendar.route) {
                CalendarScreen()
            }
        }
    }
}