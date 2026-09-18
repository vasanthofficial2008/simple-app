package com.dailygoal.reminder.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
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
    val slideEnter = fadeIn(AimlyMotionSpecs.contentTween()) + slideInHorizontally(AimlyMotionSpecs.contentTween()) { w -> w / 6 }
    val slideExit = fadeOut(AimlyMotionSpecs.contentTween()) + slideOutHorizontally(AimlyMotionSpecs.contentTween()) { w -> -w / 6 }
    val popSlideEnter = fadeIn(AimlyMotionSpecs.contentTween()) + slideInHorizontally(AimlyMotionSpecs.contentTween()) { w -> -w / 6 }
    val popSlideExit = fadeOut(AimlyMotionSpecs.contentTween()) + slideOutHorizontally(AimlyMotionSpecs.contentTween()) { w -> w / 6 }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit }
        ) {
            SplashScreen(
                isOnboardingCompleted = viewModel.isOnboardingCompleted,
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Onboarding.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit }
        ) {
            OnboardingScreen(
                onFinishOnboarding = {
                    viewModel.setOnboardingCompleted()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Home.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAddGoal = { navController.navigate(Screen.AddGoal.route) },
                onNavigateToGoalDetail = { goalId -> navController.navigate(Screen.GoalDetail.createRoute(goalId)) },
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(
            route = Screen.AddGoal.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) {
            AddEditGoalScreen(
                viewModel = viewModel,
                goalIdToEdit = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditGoal.route,
            arguments = listOf(navArgument("goalId") { type = NavType.LongType }),
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
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
            arguments = listOf(navArgument("goalId") { type = NavType.LongType }),
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong("goalId") ?: 0L
            GoalDetailScreen(
                viewModel = viewModel,
                goalId = goalId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditGoal.createRoute(id)) }
            )
        }

        composable(
            route = Screen.History.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(
            route = Screen.Statistics.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) {
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(
            route = Screen.NotificationSettings.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) {
            NotificationSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { slideEnter },
            exitTransition = { slideExit },
            popEnterTransition = { popSlideEnter },
            popExitTransition = { popSlideExit }
        ) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToNotificationSettings = { navController.navigate(Screen.NotificationSettings.route) },
                onNavigateTab = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }
    }
}
