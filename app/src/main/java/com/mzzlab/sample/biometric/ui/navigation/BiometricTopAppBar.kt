package com.mzzlab.sample.biometric.ui.navigation

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun BiometricTopAppBar(appDestination: AppDestination,
                       onUpNavigation: () -> Unit = {}) {
    val showUpNavigationBack = upNavigationRequired(appDestination)
    TopAppBar(
        title = { Text(stringResource(id = appDestination.title)) },
        navigationIcon = if(showUpNavigationBack){
            {
                IconButton(onClick = onUpNavigation) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "BackIcon",
                    )
                }
            }
        } else null,
    )
}