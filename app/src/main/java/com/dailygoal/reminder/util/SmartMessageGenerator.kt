package com.dailygoal.reminder.util

import com.dailygoal.reminder.data.model.GoalCategory

object SmartMessageGenerator {

    fun generateMessage(title: String, categoryName: String, targetCount: Int, unit: String): String {
        val category = try {
            GoalCategory.valueOf(categoryName)
        } catch (e: Exception) {
            GoalCategory.CUSTOM
        }

        return when (category) {
            GoalCategory.WATER -> "💧 Time to hydrate! Remember your goal: $title ($targetCount $unit)."
            GoalCategory.EXERCISE -> "🏃 Workout time! You planned $title ($targetCount $unit) today."
            GoalCategory.READING -> "📚 Reading goal waiting! Dive into $title for $targetCount $unit."
            GoalCategory.STUDY -> "🎯 Focused study session time! Complete $title."
            GoalCategory.MEDICINE -> "💊 Health reminder: Time to take your $title."
            GoalCategory.WORK -> "💼 Productivity booster! Time for $title."
            GoalCategory.CUSTOM -> "🔥 You've got this! Complete today's goal: $title."
        }
    }

    fun getStreakMotivation(streakCount: Int): String {
        return if (streakCount > 0) {
            "🔥 $streakCount day streak active! Keep momentum going."
        } else {
            "🚀 Start a new streak today!"
        }
    }
}
