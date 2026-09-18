package com.dailygoal.reminder.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
import com.dailygoal.reminder.ui.animation.StaggeredItemEntrance
import com.dailygoal.reminder.ui.animation.aimlyPressFeedback
import com.dailygoal.reminder.ui.animation.rememberReducedMotion
import com.dailygoal.reminder.ui.components.BottomNavBar
import com.dailygoal.reminder.ui.navigation.Screen
import com.dailygoal.reminder.ui.theme.PrimaryIndigo
import com.dailygoal.reminder.ui.theme.SuccessGreen
import com.dailygoal.reminder.ui.viewmodel.GoalViewModel
import com.dailygoal.reminder.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: GoalViewModel,
    onNavigateTab: (String) -> Unit
) {
    val completedDates by viewModel.completedDates.collectAsState()
    var currentCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDateString by remember { mutableStateOf(DateUtils.getTodayDateString()) }
    val isReducedMotion = rememberReducedMotion()

    val daysInMonth = remember(currentCalendar.timeInMillis) {
        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-based offset
        Pair(maxDays, firstDayOfWeek)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar & History",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.History.route,
                onNavigate = onNavigateTab
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar Header & Grid Card
            StaggeredItemEntrance(index = 0) {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedContent(
                                targetState = DateUtils.getMonthYearFormatted(currentCalendar),
                                transitionSpec = {
                                    if (isReducedMotion) {
                                        fadeIn(AimlyMotionSpecs.fastTween()) togetherWith fadeOut(AimlyMotionSpecs.fastTween())
                                    } else {
                                        (slideInHorizontally(AimlyMotionSpecs.fastTween()) { w -> w / 2 } + fadeIn(AimlyMotionSpecs.fastTween())) togetherWith
                                                (slideOutHorizontally(AimlyMotionSpecs.fastTween()) { w -> -w / 2 } + fadeOut(AimlyMotionSpecs.fastTween())) using
                                                SizeTransform(clip = false)
                                    }
                                },
                                label = "MonthYearTextAnim"
                            ) { monthText ->
                                Text(
                                    text = monthText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val cal = currentCalendar.clone() as Calendar
                                        cal.add(Calendar.MONTH, -1)
                                        currentCalendar = cal
                                    },
                                    modifier = Modifier.aimlyPressFeedback(pressedScale = 0.88f)
                                ) {
                                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                                }
                                IconButton(
                                    onClick = {
                                        val cal = currentCalendar.clone() as Calendar
                                        cal.add(Calendar.MONTH, 1)
                                        currentCalendar = cal
                                    },
                                    modifier = Modifier.aimlyPressFeedback(pressedScale = 0.88f)
                                ) {
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Days of week header (Sun - Sat)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Days Grid
                        val (maxDays, startOffset) = daysInMonth
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            // Empty leading cells
                            items(startOffset) {
                                Box(modifier = Modifier.size(36.dp))
                            }

                            // Month Days
                            items(maxDays) { dayIndex ->
                                val dayNum = dayIndex + 1
                                val cellCal = currentCalendar.clone() as Calendar
                                cellCal.set(Calendar.DAY_OF_MONTH, dayNum)
                                val cellDateStr = sdf.format(cellCal.time)

                                val isCompletedOnDate = completedDates.contains(cellDateStr)
                                val isSelected = cellDateStr == selectedDateString
                                val isToday = cellDateStr == DateUtils.getTodayDateString()

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> PrimaryIndigo
                                                isCompletedOnDate -> SuccessGreen.copy(alpha = 0.2f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .aimlyPressFeedback(pressedScale = 0.88f) { selectedDateString = cellDateStr }
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                        color = when {
                                            isSelected -> Color.White
                                            isCompletedOnDate -> SuccessGreen
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Details Card
            StaggeredItemEntrance(index = 1) {
                val isSelectedCompleted = completedDates.contains(selectedDateString)
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Log for $selectedDateString",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isSelectedCompleted) "✅ Goals Completed" else "⚪ No Completion Logged",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelectedCompleted) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
