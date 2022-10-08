package com.mzzlab.sample.biometric.di

import androidx.biometric.BiometricManager
import com.mzzlab.sample.biometric.data.crypto.CryptoEngine
import com.mzzlab.sample.biometric.data.BiometricRepository
import com.mzzlab.sample.biometric.data.UserRepository
import com.mzzlab.sample.biometric.data.impl.BiometricRepositoryImpl
import com.mzzlab.sample.biometric.data.impl.UserRepositoryImpl
import com.mzzlab.sample.biometric.data.storage.KeyValueStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideUserRepository( keyValueStorage: KeyValueStorage): UserRepository{
        Timber.d("provideUserRepository")
        return UserRepositoryImpl(keyValueStorage)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(
        biometricManager: BiometricManager,
        keyValueStorage: KeyValueStorage,
        cryptoEngine: CryptoEngine
    ): BiometricRepository{
        return BiometricRepositoryImpl(
            biometricManager = biometricManager,
            keyValueStorage = keyValueStorage,
            cryptoEngine = cryptoEngine
        )
    }
}