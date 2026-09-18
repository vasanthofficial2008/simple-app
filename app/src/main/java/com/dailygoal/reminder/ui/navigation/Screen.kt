package com.dailygoal.reminder.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AddGoal : Screen("add_goal")
    object EditGoal : Screen("edit_goal/{goalId}") {
        fun createRoute(goalId: Long) = "edit_goal/$goalId"
    }
    object GoalDetail : Screen("goal_detail/{goalId}") {
        fun createRoute(goalId: Long) = "goal_detail/$goalId"
    }
    object History : Screen("history")
    object Statistics : Screen("statistics")
    object NotificationSettings : Screen("notification_settings")
    object Settings : Screen("settings")
}
