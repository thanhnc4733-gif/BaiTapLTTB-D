package com.example.baitap1tuan5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                val vm: AuthViewModel = viewModel()
                val start = if (vm.user != null) "home" else "login"

                NavHost(navController = nav, startDestination = start) {
                    composable("login") {
                        LoginScreen(vm) {
                            nav.navigate("home") { popUpTo("login") { inclusive = true } }
                        }
                    }
                    composable("home") {
                        HomeScreen(vm) { nav.navigate("profile") }
                    }
                    composable("profile") {
                        ProfileScreen(
                            vm,
                            onBack = { nav.popBackStack() },
                            onLogout = {
                                nav.navigate("login") { popUpTo(0) { inclusive = true } }
                            }
                        )
                    }
                }
            }
        }
    }
}