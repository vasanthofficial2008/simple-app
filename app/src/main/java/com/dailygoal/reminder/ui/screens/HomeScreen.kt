package com.dailygoal.reminder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp
import com.dailygoal.reminder.data.model.GoalCategory
import com.dailygoal.reminder.ui.components.BottomNavBar
import com.dailygoal.reminder.ui.components.DailyReflectionCard
import com.dailygoal.reminder.ui.components.GoalCard
import com.dailygoal.reminder.ui.components.ProgressRing
import com.dailygoal.reminder.ui.components.StreakBadge
import com.dailygoal.reminder.ui.navigation.Screen
import com.dailygoal.reminder.ui.theme.PrimaryIndigo
import com.dailygoal.reminder.ui.theme.SecondaryTeal
import com.dailygoal.reminder.ui.theme.SuccessGreen
import com.dailygoal.reminder.ui.viewmodel.GoalViewModel
import com.dailygoal.reminder.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GoalViewModel,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToGoalDetail: (Long) -> Unit,
    onNavigateTab: (String) -> Unit
) {
    val todayGoals by viewModel.todayGoals.collectAsState()
    val stats by viewModel.statsSummary.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val filteredGoals = todayGoals.filter { item ->
        val matchesQuery = item.goal.title.contains(searchQuery, ignoreCase = true) ||
                item.goal.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == null || item.goal.category.equals(selectedCategoryFilter, ignoreCase = true)
        matchesQuery && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Aimly Today 🎯",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DateUtils.getTodayFormattedHeader(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    StreakBadge(streakCount = stats.currentStreak, isShieldActive = true)
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Home.route,
                onNavigate = onNavigateTab
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddGoal,
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "New Goal", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Progress Header Card
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryIndigo.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Progress Ring",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✅ ${stats.completedTodayCount} Completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "⬜ ${stats.remainingTodayCount} Remaining",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        ProgressRing(
                            progressRatio = stats.todayCompletionPercentage / 100f,
                            percentageText = "${stats.todayCompletionPercentage}%",
                            subtitleText = "Done Today",
                            size = 110.dp,
                            strokeWidth = 10.dp
                        )
                    }
                }
            }

            // Daily Reflection & Mood Card
            item {
                DailyReflectionCard()
            }

            // Upcoming Reminder Banner
            if (!stats.upcomingReminderTime.isNullOrEmpty() && !stats.upcomingGoalTitle.isNullOrEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SecondaryTeal.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Upcoming",
                                tint = SecondaryTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Upcoming Reminder: ${stats.upcomingReminderTime}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stats.upcomingGoalTitle!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Real-Time Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search your daily goals...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        focusedBorderColor = PrimaryIndigo
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Category Filter Scroll Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All Goals") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryIndigo,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(GoalCategory.values()) { category ->
                        val isSelected = selectedCategoryFilter == category.name
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryFilter = if (isSelected) null else category.name
                            },
                            label = { Text("${category.displayName}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = category.colorValue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Checklist",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredGoals.size} Goals",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Goals Checklist List
            if (filteredGoals.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎉", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedCategoryFilter != null) "No goals match your search filter" else "No daily goals set yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Tap 'New Goal' below to build your routine!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(filteredGoals, key = { it.goal.id }) { goalWithProgress ->
                    GoalCard(
                        goalWithProgress = goalWithProgress,
                        onToggleCompletion = { viewModel.toggleGoalCompletion(goalWithProgress.goal.id) },
                        onIncrementProgress = { viewModel.incrementGoalProgress(goalWithProgress.goal.id) },
                        onClick = { onNavigateToGoalDetail(goalWithProgress.goal.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
