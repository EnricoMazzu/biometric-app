package com.mzzlab.sample.biometric.di

import android.content.Context
import androidx.biometric.BiometricManager
import com.mzzlab.sample.biometric.data.crypto.CryptoEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BiometricModule {

    @Provides
    fun provideBiometricManager(@ApplicationContext context: Context): BiometricManager {
        return BiometricManager.from(context)
    }

    @Provides
    @Singleton
    fun provideCryptoEngine(): CryptoEngine {
        return CryptoEngine()
    }

}