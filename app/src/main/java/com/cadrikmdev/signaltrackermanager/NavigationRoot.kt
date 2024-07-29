package com.cadrikmdev.signaltrackermanager

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cadrikmdev.manager.presentation.manager_overview.ManagerOverviewScreenRoot
import com.cadrikmdev.permissions.presentation.screen.permissions.PermissionsScreen
import com.cadrikmdev.permissions.presentation.util.openAppSettings

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
        permissionsGraph(
            navController = navController
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
                onResolvePermissionClick = {
                    navController.navigate("permissions")
                }
            )
        }
    }
}

private fun NavGraphBuilder.permissionsGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = "permissions_screen",
        route = "permissions"
    ) {
        composable("permissions_screen") {
            val context = LocalContext.current
            PermissionsScreen(
                onBackPressed = {
                    navController.navigate("track_overview") {
                        popUpTo("permissions") {
                            inclusive = true
                        }
                    }
                },
                openAppSettings = {
                    context.openAppSettings()
                }
            )
        }
    }
}