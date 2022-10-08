package com.mzzlab.sample.biometric.di

import android.content.Context
import com.mzzlab.sample.biometric.data.storage.KeyValueStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideKeyValueStorage(@ApplicationContext context: Context): KeyValueStorage{
        val preferences = context.getSharedPreferences("simpleStorage", Context.MODE_PRIVATE)
        return KeyValueStorage(
            sharedPreferences = preferences
        )
    }
}