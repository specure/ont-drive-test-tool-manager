package com.specure.signaltrackermanager

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.specure.manager.presentation.about.AboutScreenNav
import com.specure.manager.presentation.about.AboutScreenRoot
import com.specure.manager.presentation.screens.manager_overview.ManagerOverviewScreenRoot
import com.specure.manager.presentation.screens.settings.SettingsScreenRoot
import com.specure.manager.presentation.screens.settings.navigation.SettingsScreenNav
import com.specure.permissions.presentation.screen.permissions.PermissionsScreen
import com.specure.permissions.presentation.util.openAppSettings

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
                },
                onSettingsClick = {
                    navController.navigate(SettingsScreenNav)
                },
                onAboutClick = {
                    navController.navigate(AboutScreenNav)
                },
            )
        }
        composable<SettingsScreenNav> {
            SettingsScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
            )
        }
        composable<AboutScreenNav> {
            AboutScreenRoot(
                onBackClick = {
                    navController.navigateUp()
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
                    navController.navigateUp()
                },
                openAppSettings = {
                    context.openAppSettings()
                }
            )
        }
    }
}