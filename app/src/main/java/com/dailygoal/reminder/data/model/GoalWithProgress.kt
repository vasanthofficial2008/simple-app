package com.dailygoal.reminder.data.model

import com.dailygoal.reminder.data.local.entity.GoalEntity

data class GoalWithProgress(
    val goal: GoalEntity,
    val currentProgress: Int,
    val isCompletedToday: Boolean,
    val streak: Int = 0
) {
    val progressRatio: Float
        get() = if (goal.targetCount > 0) {
            (currentProgress.toFloat() / goal.targetCount.toFloat()).coerceIn(0f, 1f)
        } else {
            if (isCompletedToday) 1f else 0f
        }
}
