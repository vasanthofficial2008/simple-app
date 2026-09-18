package com.dailygoal.reminder.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goal_completions",
    indices = [Index(value = ["goalId", "dateString"], unique = true)]
)
data class GoalCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val dateString: String, // Format: YYYY-MM-DD
    val completedCount: Int = 0,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
