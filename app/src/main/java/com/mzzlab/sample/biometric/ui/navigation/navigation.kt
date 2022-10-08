package com.mzzlab.sample.biometric.ui.navigation

import androidx.annotation.StringRes
import com.mzzlab.sample.biometric.R

interface AppDestination {
    val route: String
    val title: Int
    val requireUpNavigation: Boolean
}

object LoginRoute: AppDestination {
    override val route = "login"
    @StringRes
    override val title = R.string.login_screen_title
    override val requireUpNavigation = false
}

object HomeRoute: AppDestination {
    override val route: String = "home"
    @StringRes
    override val title = R.string.home_screen_title
    override val requireUpNavigation = false
}

fun upNavigationRequired(appDestination: AppDestination): Boolean {
    return appDestination.requireUpNavigation
}

object AppRoutes {
    @JvmStatic
    fun resolveTopLevelRoute(route: String?, fallback: AppDestination = LoginRoute): AppDestination {
        return topLevelRoutes.find {
            it.route == route
        } ?: fallback
    }

    @JvmStatic
    private var topLevelRoutes: List<AppDestination>  = listOf(LoginRoute, HomeRoute)
}