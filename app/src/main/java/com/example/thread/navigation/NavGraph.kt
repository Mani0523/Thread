package com.example.thread.navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thread.screens.AddThreads
import com.example.thread.screens.BottomNav
import com.example.thread.screens.Home
import com.example.thread.screens.Login
import com.example.thread.screens.Notification
import com.example.thread.screens.Profile
import com.example.thread.screens.Register
import com.example.thread.screens.Search
import com.example.thread.screens.Splash

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Splash.routes) {
        composable(Routes.Splash.routes) {
            Splash(navController)  // Call the composable function for the Splash screen
        }
        composable(Routes.Home.routes) {
            Home(navController)
        }
        composable(Routes.Notification.routes) {
            Notification()
        }
        composable(Routes.Search.routes) {
            Search()
        }
        composable(Routes.AddThreads.routes) {
            AddThreads(navController)
        }
        composable(Routes.Profile.routes) {
            Profile(navController)
        }
        composable(Routes.BottomNav.routes) {
            BottomNav(navController)
        }
        composable(Routes.Login.routes) {
            Login(navController) // Call the composable function for the Profile screen
        }
        composable(Routes.Register.routes) {
            Register(navController) // Call the composable function for the Profile screen
        }
    }
}