package com.mzzlab.sample.biometric

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.mzzlab.sample.biometric.ui.App
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalLifecycleComposeApi
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}