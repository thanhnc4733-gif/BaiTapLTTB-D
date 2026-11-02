package com.example.baitap1tuan5

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("FCM", "Token: $it")
        }

        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                val vm: AuthViewModel = viewModel()
                val tv: TaskViewModel = viewModel()
                val start = if (vm.user != null) "home" else "login"

                var motivation by remember { mutableStateOf("Đã hơn nửa chặng, cố lên!") }
                LaunchedEffect(Unit) {
                    val rc = FirebaseRemoteConfig.getInstance()
                    rc.setConfigSettingsAsync(
                        FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(0) // chỉ để test
                            .build()
                    )
                    rc.setDefaultsAsync(mapOf("motivation_text" to "Đã hơn nửa chặng, cố lên!"))
                    rc.fetchAndActivate().addOnCompleteListener {
                        motivation = rc.getString("motivation_text")
                    }
                }

                NavHost(navController = nav, startDestination = start) {
                    composable("login") {
                        LoginScreen(vm) {
                            nav.navigate("home") { popUpTo("login") { inclusive = true } }
                        }
                    }
                    composable("home") {
                        HomeScreen(vm, tv, motivation) { nav.navigate("profile") }
                    }
                    composable("profile") {
                        ProfileScreen(
                            vm,
                            onBack = { nav.popBackStack() },
                            onLogout = {
                                tv.stop()
                                nav.navigate("login") { popUpTo(0) { inclusive = true } }
                            }
                        )
                    }
                }
            }
        }
    }
}