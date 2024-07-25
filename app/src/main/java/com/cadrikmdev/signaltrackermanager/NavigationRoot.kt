package com.cadrikmdev.signaltrackermanager

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cadrikmdev.manager.presentation.manager_overview.ManagerOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        mainGraph(
            navController = navController,
        )
    }
}

private fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = "home",
        route = "main"
    ) {
        composable("home") {
            ManagerOverviewScreenRoot(
                onStartRunClick = {
                    // TODO:
                },
                onStopRunClick = {
                    // TODO:
                },
            )
        }
    }
}