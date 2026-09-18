package com.dailygoal.reminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailygoal.reminder.data.model.RepeatSchedule

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String, // WATER, EXERCISE, READING, etc.
    val targetCount: Int = 1,
    val unit: String = "Times",
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true,
    val repeatDaysMask: Int = RepeatSchedule.EVERYDAY,
    val colorHex: String = "#00B4D8",
    val iconName: String = "Flag",
    val isPaused: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
