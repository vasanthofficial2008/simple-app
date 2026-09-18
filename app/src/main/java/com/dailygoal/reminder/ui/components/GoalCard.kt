package com.dailygoal.reminder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailygoal.reminder.data.model.GoalCategory
import com.dailygoal.reminder.data.model.GoalWithProgress
import com.dailygoal.reminder.ui.theme.PrimaryIndigo
import com.dailygoal.reminder.ui.theme.SecondaryTeal
import com.dailygoal.reminder.ui.theme.SuccessGreen
import com.dailygoal.reminder.util.DateUtils

@Composable
fun GoalCard(
    goalWithProgress: GoalWithProgress,
    onToggleCompletion: () -> Unit,
    onIncrementProgress: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goal = goalWithProgress.goal
    val category = GoalCategory.fromName(goal.category)
    val isDone = goalWithProgress.isCompletedToday

    val cardScale by animateFloatAsState(
        targetValue = if (isDone) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    val categoryColor = category.colorValue
    val animatedCheckColor by animateColorAsState(
        targetValue = if (isDone) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "checkColor"
    )

    val cardBorder = if (isDone) {
        BorderStroke(1.5.dp, SuccessGreen.copy(alpha = 0.5f))
    } else if (goalWithProgress.streak >= 5) {
        BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(PrimaryIndigo, SecondaryTeal)))
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surface.copy(alpha = 0.85f) else MaterialTheme.colorScheme.surface
        ),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 1.dp else 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Emoji Avatar with Glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.18f))
                ) {
                    Text(
                        text = getCategoryEmojiString(category),
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title & Details
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (goal.isPaused) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Paused",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Category Pill
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = categoryColor,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(categoryColor.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        if (goal.isReminderEnabled) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Reminder",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = DateUtils.formatTime(goal.reminderHour, goal.reminderMinute),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        if (goalWithProgress.streak > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔥 ${goalWithProgress.streak}d",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Interactive Checkbox Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isDone) SuccessGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(2.dp, animatedCheckColor, CircleShape)
                        .clickable { onToggleCompletion() }
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Target Count Progress Bar
            if (goal.targetCount > 1) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Progress: ${goalWithProgress.currentProgress} / ${goal.targetCount} ${goal.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (!isDone) {
                        IconButton(
                            onClick = onIncrementProgress,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Progress",
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { goalWithProgress.progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = categoryColor,
                    trackColor = categoryColor.copy(alpha = 0.15f)
                )
            }
        }
    }
}

private fun getCategoryEmojiString(category: GoalCategory): String {
    return when (category) {
        GoalCategory.WATER -> "💧"
        GoalCategory.EXERCISE -> "🏋️"
        GoalCategory.READING -> "📚"
        GoalCategory.STUDY -> "🎓"
        GoalCategory.MEDICINE -> "💊"
        GoalCategory.WORK -> "💼"
        GoalCategory.CUSTOM -> "⭐"
    }
}
