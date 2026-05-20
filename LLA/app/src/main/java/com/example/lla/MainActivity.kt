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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lla.ui.theme.LLATheme
import com.example.lla.uis.auth.AuthViewModel
import com.example.lla.uis.auth.LanguageSelectionScreen
import com.example.lla.uis.auth.LoginScreen
import com.example.lla.uis.auth.RegisterScreen
import com.example.lla.uis.home.HomeScreen
import com.example.lla.uis.profile.LearnedVocabListScreen
import com.example.lla.uis.profile.ProfileScreen
import com.example.lla.uis.topic.FlashcardScreen
import com.example.lla.uis.topic.LessonScreen
import com.example.lla.uis.topic.TopicScreen
import com.example.lla.uis.topic.TopicViewModel

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

    val authViewModel: AuthViewModel = viewModel()
    val topicViewModel: TopicViewModel = viewModel()

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
                        modifier = Modifier.fillMaxSize(),
                        authViewModel = authViewModel,
                        topicViewModel = topicViewModel,
                        onPracticeClick = {
                            topicViewModel.prepareReviewMode()
                            navController.navigate("flashcard/review")
                        },
                        onTopicClick = { navController.navigate(AppDestinations.TOPICS.route) }
                    )
                }

                composable(AppDestinations.TOPICS.route) {
                    TopicScreen(
                        viewModel = topicViewModel,
                        modifier = Modifier.fillMaxSize(),
                        onTopicClick = { topicId ->
                            navController.navigate("lessons/$topicId")
                        }
                    )
                }

                composable(
                    route = "lessons/{topicId}",
                    arguments = listOf(navArgument("topicId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                    LessonScreen(
                        viewModel = topicViewModel,
                        topicId = topicId,
                        onBackClick = { navController.popBackStack() },
                        onLessonClick = { lessonId ->
                            navController.navigate("flashcard/$lessonId")
                        }
                    )
                }

                composable(AppDestinations.PROFILE.route) {
                    ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        authViewModel = authViewModel,
                        topicViewModel = topicViewModel,
                        onSeeAllLearnedClick = { navController.navigate("learned_list") }, // Chuyển trang
                        onReviewLearnedClick = {
                            // Chế độ ôn tập lại các từ đã học từ Profile
                            topicViewModel.prepareReviewLearnedMode()
                            navController.navigate("flashcard/review")
                        }
                    )
                }

                composable("learned_list"){
                    LearnedVocabListScreen(
                        viewModel = topicViewModel,
                        onBack = { navController.popBackStack()}
                    )
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
                        viewModel = authViewModel,
                        navController = navController
                    )
                }

                composable("register") {
                    RegisterScreen(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }

                composable(
                    route = "flashcard/{lessonId}",
                    arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                    FlashcardScreen(
                        viewModel = topicViewModel,
                        authViewModel = authViewModel,
                        topicId = lessonId,
                        onClose = { navController.popBackStack() }
                    )
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
