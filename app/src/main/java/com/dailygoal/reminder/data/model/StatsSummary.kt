package com.dailygoal.reminder.data.model

data class StatsSummary(
    val totalGoalsCount: Int = 0,
    val completedTodayCount: Int = 0,
    val remainingTodayCount: Int = 0,
    val todayCompletionPercentage: Int = 0,
    val weeklyCompletionPercentage: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val upcomingReminderTime: String? = null,
    val upcomingGoalTitle: String? = null
)
