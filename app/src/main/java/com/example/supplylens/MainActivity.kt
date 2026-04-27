package com.supplylens.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.supplylens.app.navigation.*
import com.supplylens.app.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SupplyLensTheme {
                MainScaffold()
            }
        }
    }
}

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    Scaffold(
        containerColor = NavyBg,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 0.dp
            ) {
                bottomNavScreens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(text = screen.icon, fontSize = 18.sp) },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = CyanAccent,
                            selectedTextColor   = CyanAccent,
                            indicatorColor      = CyanAccent.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AppNavGraph(navController = navController)

            // Floating AI cursor indicator
            AiCursorOverlay()
        }
    }
}
@Composable
fun AiCursorOverlay() {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(800); visible = !visible
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
        Row(
            modifier = Modifier.padding(12.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (visible) CyanAccent else CyanAccent.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )
            Spacer(Modifier.width(4.dp))
            Text("AI LIVE", color = CyanAccent, fontSize = 9.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}