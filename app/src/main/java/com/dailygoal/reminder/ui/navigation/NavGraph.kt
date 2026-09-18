package com.dailygoal.reminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dailygoal.reminder.ui.screens.AddEditGoalScreen
import com.dailygoal.reminder.ui.screens.GoalDetailScreen
import com.dailygoal.reminder.ui.screens.HistoryScreen
import com.dailygoal.reminder.ui.screens.HomeScreen
import com.dailygoal.reminder.ui.screens.NotificationSettingsScreen
import com.dailygoal.reminder.ui.screens.OnboardingScreen
import com.dailygoal.reminder.ui.screens.SettingsScreen
import com.dailygoal.reminder.ui.screens.SplashScreen
import com.dailygoal.reminder.ui.screens.StatisticsScreen
import com.dailygoal.reminder.ui.viewmodel.GoalViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: GoalViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isOnboardingCompleted = viewModel.isOnboardingCompleted,
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    viewModel.setOnboardingCompleted()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAddGoal = { navController.navigate(Screen.AddGoal.route) },
                onNavigateToGoalDetail = { goalId -> navController.navigate(Screen.GoalDetail.createRoute(goalId)) },
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Screen.AddGoal.route) {
            AddEditGoalScreen(
                viewModel = viewModel,
                goalIdToEdit = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditGoal.route,
            arguments = listOf(navArgument("goalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong("goalId") ?: 0L
            AddEditGoalScreen(
                viewModel = viewModel,
                goalIdToEdit = goalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.GoalDetail.route,
            arguments = listOf(navArgument("goalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong("goalId") ?: 0L
            GoalDetailScreen(
                viewModel = viewModel,
                goalId = goalId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditGoal.createRoute(id)) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToNotificationSettings = { navController.navigate(Screen.NotificationSettings.route) },
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }
    }
}
