package com.dailygoal.reminder.data.model

import androidx.compose.ui.graphics.Color

enum class GoalCategory(
    val displayName: String,
    val iconName: String,
    val defaultUnit: String,
    val colorHex: String,
    val colorValue: Color
) {
    WATER("Water Hydration", "LocalDrinkingWater", "Liters", "#00B4D8", Color(0xFF00B4D8)),
    EXERCISE("Fitness & Exercise", "FitnessCenter", "Minutes", "#FF6B6B", Color(0xFFFF6B6B)),
    READING("Reading Books", "MenuBook", "Pages", "#9D4EDD", Color(0xFF9D4EDD)),
    STUDY("Study & Learning", "School", "Hours", "#4895EF", Color(0xFF4895EF)),
    MEDICINE("Health & Medicine", "Medication", "Doses", "#2EC4B6", Color(0xFF2EC4B6)),
    WORK("Work & Productivity", "Work", "Tasks", "#FF9F1C", Color(0xFFFF9F1C)),
    CUSTOM("Custom Goal", "Flag", "Times", "#4361EE", Color(0xFF4361EE));

    companion object {
        fun fromName(name: String): GoalCategory {
            return try {
                valueOf(name)
            } catch (e: Exception) {
                CUSTOM
            }
        }
    }
}
