package com.mzzlab.sample.biometric.data
import kotlinx.coroutines.flow.StateFlow

/**
 * User repository
 */
interface UserRepository {

    /**
     * Flow that contains if the user is logged in or not
     */
    val isUserLoggedIn: StateFlow<Boolean>

    /**
     *
     */
    suspend fun login(username: String, password: String)

    /**
     *
     */
    suspend fun loginWithToken(token: String)

    /**
     *
     */
    suspend fun logout()
}