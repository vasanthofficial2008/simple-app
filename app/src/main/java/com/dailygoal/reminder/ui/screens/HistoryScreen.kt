package com.dailygoal.reminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        text = "History & Calendar",
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
            // Calendar Header Controls
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = DateUtils.getMonthYearFormatted(currentCalendar),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            IconButton(onClick = {
                                val cal = currentCalendar.clone() as Calendar
                                cal.add(Calendar.MONTH, -1)
                                currentCalendar = cal
                            }) {
                                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                            }
                            IconButton(onClick = {
                                val cal = currentCalendar.clone() as Calendar
                                cal.add(Calendar.MONTH, 1)
                                currentCalendar = cal
                            }) {
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
                                style = MaterialTheme.typography.labelLarge,
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
                                            isCompletedOnDate -> SuccessGreen.copy(alpha = 0.25f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { selectedDateString = cellDateStr }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayNum",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color.White
                                            isToday -> PrimaryIndigo
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (isCompletedOnDate && !isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(SuccessGreen)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Date Summary
            Text(
                text = "Log for ${DateUtils.formatDateForDisplay(selectedDateString)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            val isDateDone = completedDates.contains(selectedDateString)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isDateDone) "🎉 Goals completed on this date!" else "📅 No completed goals logged on this date.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDateDone) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = if (isDateDone) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
