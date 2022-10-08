package com.mzzlab.sample.biometric.ui

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mzzlab.sample.biometric.ui.snackbar.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Represent the app state
 */
class AppState(
    val navController: NavHostController,
    val scaffoldState: ScaffoldState,
    private val coroutineScope: CoroutineScope,
    private val resources: Resources,
    private val snackbarManager: SnackbarManager,
){
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                if (currentMessages.isNotEmpty()) {
                    val message = currentMessages[0]
                    val text = if(message.messageResId != null) {
                        resources.getText(message.messageResId)
                    }else{
                        message.messageText
                    }

                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    scaffoldState.snackbarHostState.showSnackbar(text.toString())
                    // Once the snackbar is gone or dismissed, notify the SnackbarManager
                    snackbarManager.setMessageShown(message.id)
                }
            }
        }
    }
}

/**
 * Utility function that provide the remembered [AppState] instance
 * @param navController: the app nav controller
 * @param scaffoldState: the state of the Scaffold
 * @param coroutineScope
 * @param resources
 */
@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    resources: Resources = resourcesAsComposable(),
) = remember(navController, scaffoldState, coroutineScope, resources) {
    AppState(navController, scaffoldState, coroutineScope, resources, SnackbarManager)
}

@Composable
@ReadOnlyComposable
private fun resourcesAsComposable(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
