package com.dailygoal.reminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailygoal.reminder.ui.components.BottomNavBar
import com.dailygoal.reminder.ui.navigation.Screen
import com.dailygoal.reminder.ui.theme.PrimaryIndigo
import com.dailygoal.reminder.ui.theme.SecondaryTeal
import com.dailygoal.reminder.ui.theme.SuccessGreen
import com.dailygoal.reminder.ui.theme.WarningOrange
import com.dailygoal.reminder.ui.viewmodel.GoalViewModel
import com.dailygoal.reminder.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: GoalViewModel,
    onNavigateTab: (String) -> Unit
) {
    val stats by viewModel.statsSummary.collectAsState()
    val completedDates by viewModel.completedDates.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Progress & Analytics 📊",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Statistics.route,
                onNavigate = onNavigateTab
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview Streaks Card with Accent Gradient
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigo),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Habit Streak",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${stats.currentStreak}",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "days 🔥",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(text = "⚡", fontSize = 38.sp)
                    }
                }
            }

            // Today Completion Progress Bar Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Today's Completion Rate",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${stats.todayCompletionPercentage}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (stats.todayCompletionPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = PrimaryIndigo,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${stats.completedTodayCount} out of ${stats.totalGoalsCount} daily goals completed today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Last 7 Days Visual Heatmap Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Last 7 Days Consistency Grid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val past7Days = remember { DateUtils.getPastDays(7) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        past7Days.forEach { dateStr ->
                            val isDone = completedDates.contains(dateStr)
                            val barHeight = if (isDone) 85.dp else 28.dp
                            val barColor = if (isDone) SecondaryTeal else MaterialTheme.colorScheme.surfaceVariant

                            val dateLabel = if (dateStr.length >= 2) dateStr.substring(dateStr.length - 2) else dateStr

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .width(32.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(barColor)
                                ) {
                                    if (isDone) {
                                        Text(text = "✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // Milestone Achievements Section
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Badges & Achievements 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AchievementBadge(emoji = "⚡", title = "7-Day Streak", isUnlocked = stats.currentStreak >= 7)
                        AchievementBadge(emoji = "🏆", title = "100 Goals", isUnlocked = stats.completedTodayCount >= 1)
                        AchievementBadge(emoji = "🛡️", title = "Streak Shield", isUnlocked = true)
                        AchievementBadge(emoji = "👑", title = "Master Focus", isUnlocked = stats.todayCompletionPercentage == 100)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun AchievementBadge(
    emoji: String,
    title: String,
    isUnlocked: Boolean
) {
    val alpha = if (isUnlocked) 1.0f else 0.4f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) WarningOrange.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = if (isUnlocked) WarningOrange else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Text(text = emoji, fontSize = 24.sp, modifier = Modifier.clip(CircleShape))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
        )
    }
}
