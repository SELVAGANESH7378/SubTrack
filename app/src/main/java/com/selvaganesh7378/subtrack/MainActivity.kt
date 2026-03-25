package com.selvaganesh7378.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.selvaganesh7378.subtrack.ui.screens.navgraph.RootNavGraph
import com.selvaganesh7378.subtrack.ui.theme.SubTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubTrackTheme {
                val navController = rememberNavController()
                RootNavGraph(navController = navController)
            }
        }
    }
}

