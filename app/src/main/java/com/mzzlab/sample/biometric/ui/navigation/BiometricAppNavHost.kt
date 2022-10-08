package com.mzzlab.sample.biometric.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mzzlab.sample.biometric.ui.screen.home.addHomeRoute
import com.mzzlab.sample.biometric.ui.screen.login.addLoginRoute

/**
 * Our app NavHost wrapper
 */
@ExperimentalLifecycleComposeApi
@Composable
fun BiometricAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = LoginRoute.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ){
        addLoginRoute(navController)
        addHomeRoute(navController)
    }
}