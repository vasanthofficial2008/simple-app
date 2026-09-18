package com.dailygoal.reminder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailygoal.reminder.data.model.GoalCategory
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
import com.dailygoal.reminder.ui.animation.aimlyPressFeedback
import com.dailygoal.reminder.ui.animation.rememberReducedMotion

@Composable
fun CategoryChip(
    category: GoalCategory,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReducedMotion = rememberReducedMotion()

    val scale by animateFloatAsState(
        targetValue = if (isSelected && !isReducedMotion) 1.04f else 1.0f,
        animationSpec = AimlyMotionSpecs.ChipSelectSpring,
        label = "ChipSelectScale"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) category.colorValue else category.colorValue.copy(alpha = 0.12f),
        animationSpec = AimlyMotionSpecs.fastTween(),
        label = "ChipBgColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else category.colorValue,
        animationSpec = AimlyMotionSpecs.fastTween(),
        label = "ChipTextColor"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) category.colorValue else category.colorValue.copy(alpha = 0.25f),
        animationSpec = AimlyMotionSpecs.fastTween(),
        label = "ChipBorderColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBgColor)
            .border(1.dp, animatedBorderColor, RoundedCornerShape(16.dp))
            .aimlyPressFeedback(pressedScale = 0.94f) { onSelect() }
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
            color = animatedTextColor
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
