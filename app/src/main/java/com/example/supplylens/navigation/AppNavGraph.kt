package com.supplylens.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.supplylens.app.ui.screens.aiexplain.AiExplainScreen
import com.supplylens.app.ui.screens.cascademap.CascadeMapScreen
import com.supplylens.app.ui.screens.dashboard.DashboardScreen
import com.supplylens.app.ui.screens.routecheck.RouteCheckScreen

sealed class Screen(val route: String, val label: String, val icon: String) {
    object Dashboard  : Screen("dashboard",   "Dashboard",  "📊")
    object CascadeMap : Screen("cascade_map", "Cascade",    "🌐")
    object RouteCheck : Screen("route_check", "Routes",     "🔍")
    object AiExplain  : Screen("ai_explain",  "AI Insight", "🤖")
}

val bottomNavScreens = listOf(
    Screen.Dashboard, Screen.CascadeMap, Screen.RouteCheck, Screen.AiExplain
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route)  { DashboardScreen() }
        composable(Screen.CascadeMap.route) { CascadeMapScreen() }
        composable(Screen.RouteCheck.route) { RouteCheckScreen() }
        composable(Screen.AiExplain.route)  { AiExplainScreen() }
    }
}