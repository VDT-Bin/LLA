package com.example.lla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lla.ui.theme.LLATheme
import com.example.lla.uis.auth.AuthViewModel
import com.example.lla.uis.auth.LanguageSelectionScreen
import com.example.lla.uis.auth.LoginScreen
import com.example.lla.uis.auth.RegisterScreen
import com.example.lla.uis.home.HomeScreen
import com.example.lla.uis.profile.ProfileScreen
import com.example.lla.uis.topic.FlashcardScreen
import com.example.lla.uis.topic.TopicScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LLATheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = AppDestinations.entries.any { it.route == currentDestination?.route }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            if (showBottomBar) {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppDestinations.HOME.route) {
                    HomeScreen(
                        onPracticeClick = { navController.navigate("flashcard") },
                        onTopicClick = { navController.navigate(AppDestinations.TOPICS.route) }
                    )
                }

                composable(AppDestinations.TOPICS.route) {
                    TopicScreen(modifier = Modifier.fillMaxSize()) {
                        navController.navigate("flashcard")
                    }
                }

                composable(AppDestinations.PROFILE.route) {
                    ProfileScreen(modifier = Modifier.fillMaxSize(), navController)
                }

                composable("language_selection") {
                    LanguageSelectionScreen(
                        onContinue = {
                            navController.navigate(AppDestinations.HOME.route) {
                                popUpTo("language_selection") { inclusive = true }
                            }
                        }
                    )
                }

                composable("login") {
                    LoginScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = AuthViewModel(),
                        navController = navController
                    )
                }

                composable("register") {
                    RegisterScreen(
                        modifier = Modifier.fillMaxSize()
                        ,navController
                    )
                }

                composable("flashcard") {
                    FlashcardScreen(onClose = { navController.popBackStack() })
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    HOME("Home", Icons.Default.Home, "home"),
    TOPICS("Topics", Icons.AutoMirrored.Filled.MenuBook, "topics"),
    PROFILE("Profile", Icons.Default.AccountCircle, "profile"),
}
