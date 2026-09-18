package com.dailygoal.reminder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailygoal.reminder.data.model.GoalCategory
import com.dailygoal.reminder.data.model.GoalWithProgress
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
import com.dailygoal.reminder.ui.animation.AnimatedCheckmarkIcon
import com.dailygoal.reminder.ui.animation.AnimatedProgressCounter
import com.dailygoal.reminder.ui.animation.aimlyPressFeedback
import com.dailygoal.reminder.ui.animation.specOrSnap
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

    val categoryColor = category.colorValue

    val animatedCheckColor by animateColorAsState(
        targetValue = if (isDone) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
        animationSpec = AimlyMotionSpecs.fastTween(),
        label = "CheckColorAnim"
    )

    val animatedCheckBg by animateColorAsState(
        targetValue = if (isDone) SuccessGreen.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = AimlyMotionSpecs.fastTween(),
        label = "CheckBgAnim"
    )

    val cardBorder = when {
        isDone -> BorderStroke(1.5.dp, SuccessGreen.copy(alpha = 0.6f))
        goalWithProgress.streak >= 5 -> BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(PrimaryIndigo, SecondaryTeal)))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    }

    val animatedProgressRatio by animateFloatAsState(
        targetValue = goalWithProgress.progressRatio,
        animationSpec = specOrSnap(AimlyMotionSpecs.ProgressSpring),
        label = "ProgressRatioAnim"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surface.copy(alpha = 0.88f) else MaterialTheme.colorScheme.surface
        ),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDone) 1.dp else 3.dp,
            pressedElevation = 1.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = AimlyMotionSpecs.contentTween())
            .aimlyPressFeedback(pressedScale = 0.97f, onClick = onClick)
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
                // Category Avatar with Clean Soft Background
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(categoryColor.copy(alpha = 0.16f))
                ) {
                    Text(
                        text = getCategoryEmojiString(category),
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title & Metadata Stack
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (goal.isPaused) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Paused",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(categoryColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor
                            )
                        }

                        // Reminder Time
                        if (goal.isReminderEnabled) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Reminder",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = DateUtils.formatTime(goal.reminderHour, goal.reminderMinute),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Animated Streak Indicator
                        AnimatedVisibility(
                            visible = goalWithProgress.streak > 0,
                            enter = fadeIn(AimlyMotionSpecs.fastTween()) + scaleIn(AimlyMotionSpecs.HeroSpringFloat),
                            exit = fadeOut(AimlyMotionSpecs.fastTween()) + scaleOut()
                        ) {
                            Text(
                                text = "🔥 ${goalWithProgress.streak}d",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Checkbox Action Button with Spring Feedback
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(animatedCheckBg)
                        .border(2.dp, animatedCheckColor, CircleShape)
                        .aimlyPressFeedback(pressedScale = 0.88f) { onToggleCompletion() }
                ) {
                    AnimatedCheckmarkIcon(
                        checked = isDone,
                        size = 24.dp,
                        tint = SuccessGreen
                    )
                }
            }

            // Target Progress Bar (if targetCount > 1)
            if (goal.targetCount > 1) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedProgressCounter(
                        valueText = "${goalWithProgress.currentProgress} / ${goal.targetCount} ${goal.unit}",
                        textStyle = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (!isDone) {
                        IconButton(
                            onClick = onIncrementProgress,
                            modifier = Modifier
                                .size(30.dp)
                                .aimlyPressFeedback(pressedScale = 0.85f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Progress",
                                tint = categoryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
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
