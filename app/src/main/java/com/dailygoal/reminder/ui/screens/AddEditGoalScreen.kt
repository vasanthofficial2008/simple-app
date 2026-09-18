package com.dailygoal.reminder.ui.screens

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailygoal.reminder.data.local.entity.GoalEntity
import com.dailygoal.reminder.data.model.GoalCategory
import com.dailygoal.reminder.data.model.RepeatSchedule
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
import com.dailygoal.reminder.ui.animation.StaggeredItemEntrance
import com.dailygoal.reminder.ui.animation.aimlyPressFeedback
import com.dailygoal.reminder.ui.components.CategoryChip
import com.dailygoal.reminder.ui.components.DaySelector
import com.dailygoal.reminder.ui.theme.PrimaryIndigo
import com.dailygoal.reminder.ui.viewmodel.GoalViewModel
import com.dailygoal.reminder.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(
    viewModel: GoalViewModel,
    goalIdToEdit: Long? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val allGoals by viewModel.allGoals.collectAsState()
    val existingGoal = remember(goalIdToEdit, allGoals) {
        if (goalIdToEdit != null && goalIdToEdit > 0) {
            allGoals.find { it.id == goalIdToEdit }
        } else null
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GoalCategory.WATER) }
    var targetCountText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf(GoalCategory.WATER.defaultUnit) }
    var reminderHour by remember { mutableIntStateOf(9) }
    var reminderMinute by remember { mutableIntStateOf(0) }
    var isReminderEnabled by remember { mutableStateOf(true) }
    var repeatDaysMask by remember { mutableIntStateOf(RepeatSchedule.EVERYDAY) }

    LaunchedEffect(existingGoal) {
        existingGoal?.let { g ->
            title = g.title
            description = g.description
            selectedCategory = GoalCategory.fromName(g.category)
            targetCountText = g.targetCount.toString()
            unit = g.unit
            reminderHour = g.reminderHour
            reminderMinute = g.reminderMinute
            isReminderEnabled = g.isReminderEnabled
            repeatDaysMask = g.repeatDaysMask
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingGoal != null) "Edit Goal" else "Create Daily Goal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.aimlyPressFeedback(pressedScale = 0.88f)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Preset Category Selector Header & Row
            StaggeredItemEntrance(index = 0) {
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(GoalCategory.values()) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = selectedCategory == category,
                                onSelect = {
                                    selectedCategory = category
                                    if (title.isBlank()) {
                                        title = when (category) {
                                            GoalCategory.WATER -> "Drink 2L Water"
                                            GoalCategory.EXERCISE -> "Exercise 30 Mins"
                                            GoalCategory.READING -> "Read 10 Pages"
                                            GoalCategory.STUDY -> "Study 1 Hour"
                                            GoalCategory.MEDICINE -> "Take Medicine"
                                            GoalCategory.WORK -> "Complete Work Tasks"
                                            GoalCategory.CUSTOM -> "My Daily Goal"
                                        }
                                    }
                                    unit = category.defaultUnit
                                }
                            )
                        }
                    }
                }
            }

            // Goal Title Input
            StaggeredItemEntrance(index = 1) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Name *") },
                    placeholder = { Text("e.g. Drink 2L Water") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Description Input
            StaggeredItemEntrance(index = 2) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("e.g. Stay hydrated throughout the day") },
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Target Count & Unit Pair
            StaggeredItemEntrance(index = 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = targetCountText,
                        onValueChange = { targetCountText = it.filter { c -> c.isDigit() } },
                        label = { Text("Target Count") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        placeholder = { Text("e.g. Liters, Mins") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }

            // Reminder Time Card Container
            StaggeredItemEntrance(index = 4) {
                Card(
                    shape = RoundedCornerShape(20.dp),
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
                            Column {
                                Text(
                                    text = "Daily Push Reminder",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Receive background notification",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = isReminderEnabled,
                                onCheckedChange = { isReminderEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryIndigo)
                            )
                        }

                        AnimatedVisibility(
                            visible = isReminderEnabled,
                            enter = expandVertically(AimlyMotionSpecs.contentTween()) + fadeIn(AimlyMotionSpecs.fastTween()),
                            exit = shrinkVertically(AimlyMotionSpecs.contentTween()) + fadeOut(AimlyMotionSpecs.fastTween())
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                reminderHour = hour
                                                reminderMinute = minute
                                            },
                                            reminderHour,
                                            reminderMinute,
                                            false
                                        ).show()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo.copy(alpha = 0.12f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aimlyPressFeedback(pressedScale = 0.95f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Time",
                                        tint = PrimaryIndigo
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Scheduled Time: ${DateUtils.formatTime(reminderHour, reminderMinute)}",
                                        color = PrimaryIndigo,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Repeat Days Selector Container
            StaggeredItemEntrance(index = 5) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Repeat Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DaySelector(
                            selectedDaysMask = repeatDaysMask,
                            onMaskChanged = { repeatDaysMask = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Goal CTA Button
            StaggeredItemEntrance(index = 6) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val countInt = targetCountText.toIntOrNull() ?: 1
                            val newOrUpdatedGoal = GoalEntity(
                                id = existingGoal?.id ?: 0L,
                                title = title.trim(),
                                description = description.trim(),
                                category = selectedCategory.name,
                                targetCount = countInt,
                                unit = if (unit.isNotBlank()) unit.trim() else "Times",
                                reminderHour = reminderHour,
                                reminderMinute = reminderMinute,
                                isReminderEnabled = isReminderEnabled,
                                repeatDaysMask = repeatDaysMask,
                                colorHex = selectedCategory.colorHex,
                                iconName = selectedCategory.iconName,
                                isPaused = existingGoal?.isPaused ?: false
                            )
                            if (existingGoal != null) {
                                viewModel.updateGoal(newOrUpdatedGoal)
                            } else {
                                viewModel.addGoal(newOrUpdatedGoal)
                            }
                            onNavigateBack()
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .aimlyPressFeedback(pressedScale = 0.95f)
                ) {
                    Text(
                        text = if (existingGoal != null) "Update Goal" else "Save & Schedule Goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
