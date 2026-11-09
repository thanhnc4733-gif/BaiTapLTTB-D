package com.example.baitap1tuan5

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("FCM", "Token: $it")
        }
        val prefs = getSharedPreferences("app", MODE_PRIVATE)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val nav = rememberNavController()
                val vm: AuthViewModel = viewModel()
                val tv: TaskViewModel = viewModel()

                val onboarded = prefs.getBoolean("onboarded", false)
                val afterLogin = if (onboarded) "home" else "permissions"
                val start = if (vm.user == null) "login" else afterLogin

                // Thông báo lỗi API (hoàn tác)
                LaunchedEffect(tv.notice) {
                    tv.notice?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        tv.clearNotice()
                    }
                }

                // Remote Config
                var msgStart by remember { mutableStateOf("Bắt đầu thôi nào, bạn làm được!") }
                var msgHalf by remember { mutableStateOf("Đã hơn nửa chặng, cố lên!") }
                var msgDone by remember { mutableStateOf("Tuyệt vời, bạn đã hoàn thành hết!") }
                LaunchedEffect(Unit) {
                    val rc = FirebaseRemoteConfig.getInstance()
                    rc.setConfigSettingsAsync(
                        FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(0)   // chỉ để test
                            .build()
                    )
                    rc.setDefaultsAsync(
                        mapOf("msg_start" to msgStart, "msg_half" to msgHalf, "msg_done" to msgDone)
                    )
                    rc.fetchAndActivate().addOnCompleteListener {
                        msgStart = rc.getString("msg_start")
                        msgHalf = rc.getString("msg_half")
                        msgDone = rc.getString("msg_done")
                    }
                }

                NavHost(navController = nav, startDestination = start) {
                    composable("login") {
                        LoginScreen(vm) {
                            nav.navigate(afterLogin) { popUpTo("login") { inclusive = true } }
                        }
                    }
                    composable("permissions") {
                        PermissionsScreen {
                            prefs.edit().putBoolean("onboarded", true).apply()
                            nav.navigate("home") { popUpTo("permissions") { inclusive = true } }
                        }
                    }
                    composable("home") { HomeScreen(vm, tv, nav, msgStart, msgHalf, msgDone) }
                    composable("list") { ListScreen(tv, nav) }
                    composable("settings") { SettingsScreen(tv, nav) }
                    composable("add") { AddTaskScreen(tv, nav) }
                    composable(
                        "detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { entry ->
                        DetailScreen(tv, entry.arguments?.getInt("id") ?: 0, nav)
                    }
                    composable("profile") {
                        ProfileScreen(vm, tv, nav) {
                            tv.reset()
                            nav.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    }
                }
            }
        }
    }
}
