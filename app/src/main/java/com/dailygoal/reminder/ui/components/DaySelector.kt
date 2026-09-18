package com.dailygoal.reminder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailygoal.reminder.ui.theme.PrimaryIndigo
import com.dailygoal.reminder.util.DateUtils

@Composable
fun DaySelector(
    selectedDaysMask: Int,
    onMaskChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        dayLabels.forEachIndexed { index, label ->
            val isSelected = DateUtils.isDaySelectedInMask(selectedDaysMask, index)
            val bgColor = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant
            val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable {
                        val newMask = selectedDaysMask xor (1 shl index)
                        onMaskChanged(newMask)
                    }
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
