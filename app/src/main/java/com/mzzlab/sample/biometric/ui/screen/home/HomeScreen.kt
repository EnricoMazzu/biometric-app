package com.mzzlab.sample.biometric.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mzzlab.sample.biometric.R
import com.mzzlab.sample.biometric.ui.navigation.HomeRoute
import com.mzzlab.sample.biometric.ui.navigation.LoginRoute

@ExperimentalLifecycleComposeApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogoutDone: () -> Unit = {}
) {

    val uiState: HomeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    if(!uiState.loggedIn){
        LaunchedEffect(key1 = Unit){
            onLogoutDone()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.home_content_message))
        Divider(Modifier.width(8.dp))
        Button(onClick = {
            viewModel.logout()
        }) {
            Text(text = stringResource(id = R.string.logout_button_text))
        }
    }
}

@ExperimentalLifecycleComposeApi
fun NavGraphBuilder.addHomeRoute(navController: NavHostController) {
    composable(
        route = HomeRoute.route
    ) {
        val viewModel: HomeViewModel = hiltViewModel()
        HomeScreen(
            viewModel = viewModel,
            onLogoutDone = {
                navController.navigate(
                    route = LoginRoute.route,
                    navOptions = NavOptions
                        .Builder()
                        .setPopUpTo(HomeRoute.route, inclusive = true)
                        .build()
                )
            }
        )
    }
}