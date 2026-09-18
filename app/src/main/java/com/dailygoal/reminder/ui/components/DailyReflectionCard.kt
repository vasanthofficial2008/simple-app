package com.dailygoal.reminder.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryIndigo.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.2f)),
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
                        text = "Daily Mood & Focus 🌟",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "How are your energy levels today?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                moods.forEach { mood ->
                    val isSelected = selectedMood == mood.emoji
                    val bgColor = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surface
                    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedMood = mood.emoji }
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Text(text = mood.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mood.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            AnimatedVisibility(visible = selectedMood != null) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "✨ Logged! Keep building great momentum today.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryIndigo
                    )
                }
            }
        }
    }
}
