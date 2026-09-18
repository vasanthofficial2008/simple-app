package com.dailygoal.reminder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailygoal.reminder.data.model.GoalCategory

@Composable
fun CategoryChip(
    category: GoalCategory,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) category.colorValue else category.colorValue.copy(alpha = 0.12f)
    val textColor = if (isSelected) Color.White else category.colorValue
    val borderColor = if (isSelected) category.colorValue else category.colorValue.copy(alpha = 0.3f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = getCategoryEmoji(category),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

fun getCategoryEmoji(category: GoalCategory): String {
    return when (category) {
        GoalCategory.WATER -> "💧"
        GoalCategory.EXERCISE -> "🏃"
        GoalCategory.READING -> "📚"
        GoalCategory.STUDY -> "🎓"
        GoalCategory.MEDICINE -> "💊"
        GoalCategory.WORK -> "💼"
        GoalCategory.CUSTOM -> "🎯"
    }
}
