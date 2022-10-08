package com.mzzlab.sample.biometric.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mzzlab.sample.biometric.ui.navigation.*
import com.mzzlab.sample.biometric.ui.theme.BiometricAppTheme

/**
 * The app composable.
 * The structure is: [Scaffold] -> [BiometricAppNavHost] ->
 * [androidx.navigation.compose.NavHost] -> Routes...
 */
@ExperimentalLifecycleComposeApi
@Composable
fun App() {
    BiometricAppTheme {
        val appState = rememberAppState()
        val currentBackStack by appState.navController.currentBackStackEntryAsState()
        val appDestination = resolveAppDestination(currentBackStack)
        Scaffold(
            topBar = {
                BiometricTopAppBar(
                    appDestination = appDestination,
                    onUpNavigation = {
                        appState.navController.popBackStack()
                    }
                )
            },
            scaffoldState = appState.scaffoldState) {
            BiometricAppNavHost (
                Modifier.padding(it),
                navController = appState.navController,
            )
        }
    }
}

fun resolveAppDestination(currentBackStack: NavBackStackEntry?): AppDestination {
    return AppRoutes.resolveTopLevelRoute(
        route = currentBackStack?.destination?.route,
        fallback = LoginRoute
    )
}
