package com.dailygoal.reminder.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.sp
import com.dailygoal.reminder.ui.theme.SecondaryTeal
import com.dailygoal.reminder.ui.theme.WarningOrange

@Composable
fun StreakBadge(
    streakCount: Int,
    isShieldActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isHighStreak = streakCount >= 7
    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isHighStreak) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val backgroundColor = if (isHighStreak) WarningOrange.copy(alpha = 0.22f) else WarningOrange.copy(alpha = 0.12f)
    val borderColor = if (isHighStreak) WarningOrange else Color.Transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .scale(pulseScale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(width = if (isHighStreak) 1.dp else 0.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = if (isHighStreak) "⚡" else "🔥", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (streakCount > 0) "$streakCount Day Streak" else "No Streak Yet",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = WarningOrange
        )
        if (isShieldActive) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "🛡️", fontSize = 14.sp)
        }
    }
}
