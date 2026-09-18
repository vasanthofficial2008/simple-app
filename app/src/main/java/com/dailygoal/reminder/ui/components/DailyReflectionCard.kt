package com.dailygoal.reminder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
import com.dailygoal.reminder.ui.animation.aimlyPressFeedback
import com.dailygoal.reminder.ui.animation.rememberReducedMotion
import com.dailygoal.reminder.ui.theme.PrimaryIndigo

data class MoodOption(val emoji: String, val label: String)

val moods = listOf(
    MoodOption("🚀", "Super Focus"),
    MoodOption("😊", "Good Pace"),
    MoodOption("☕", "Steady"),
    MoodOption("🔋", "Low Battery")
)

@Composable
fun DailyReflectionCard(
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf<String?>(null) }
    val isReducedMotion = rememberReducedMotion()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Daily Reflection & Energy 🌿",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "How are your focus levels feeling today?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                moods.forEach { mood ->
                    val isSelected = selectedMood == mood.emoji

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected && !isReducedMotion) 1.04f else 1.0f,
                        animationSpec = AimlyMotionSpecs.ChipSelectSpring,
                        label = "MoodScale"
                    )

                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        animationSpec = AimlyMotionSpecs.fastTween(),
                        label = "MoodBg"
                    )

                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        animationSpec = AimlyMotionSpecs.fastTween(),
                        label = "MoodText"
                    )

                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        animationSpec = AimlyMotionSpecs.fastTween(),
                        label = "MoodBorder"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                            .aimlyPressFeedback(pressedScale = 0.93f) { selectedMood = mood.emoji }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(text = mood.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mood.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            color = textColor
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedMood != null,
                enter = fadeIn(AimlyMotionSpecs.fastTween()),
                exit = fadeOut(AimlyMotionSpecs.fastTween())
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Reflected mood saved: $selectedMood",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
