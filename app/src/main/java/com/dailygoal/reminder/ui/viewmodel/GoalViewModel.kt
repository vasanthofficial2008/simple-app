package com.dailygoal.reminder.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailygoal.reminder.data.local.AppDatabase
import com.dailygoal.reminder.data.local.entity.GoalEntity
import com.dailygoal.reminder.data.local.preferences.PreferencesManager
import com.dailygoal.reminder.data.local.repository.GoalRepository
import com.dailygoal.reminder.data.model.GoalWithProgress
import com.dailygoal.reminder.data.model.StatsSummary
import com.dailygoal.reminder.notification.AlarmScheduler
import com.dailygoal.reminder.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val alarmScheduler = AlarmScheduler(application)
    val repository = GoalRepository(db.goalDao(), db.goalCompletionDao(), alarmScheduler)
    val prefsManager = PreferencesManager(application)

    val todayGoals: StateFlow<List<GoalWithProgress>> = repository.getTodayGoalsWithProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val statsSummary: StateFlow<StatsSummary> = repository.getStatsSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsSummary())

    val completedDates: StateFlow<List<String>> = db.goalCompletionDao().getAllCompletedDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _themeMode = MutableStateFlow(prefsManager.themeMode)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _globalNotifications = MutableStateFlow(prefsManager.isGlobalNotificationsEnabled)
    val globalNotifications: StateFlow<Boolean> = _globalNotifications.asStateFlow()

    private val _snoozeDuration = MutableStateFlow(prefsManager.snoozeDurationMinutes)
    val snoozeDuration: StateFlow<Int> = _snoozeDuration.asStateFlow()

    val isOnboardingCompleted: Boolean
        get() = prefsManager.isOnboardingCompleted

    fun setOnboardingCompleted() {
        prefsManager.isOnboardingCompleted = true
    }

    fun addGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun toggleGoalPause(goal: GoalEntity) {
        viewModelScope.launch {
            repository.toggleGoalPause(goal)
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun toggleGoalCompletion(goalId: Long) {
        viewModelScope.launch {
            repository.toggleGoalCompletion(goalId, DateUtils.getTodayDateString())
        }
    }

    fun incrementGoalProgress(goalId: Long) {
        viewModelScope.launch {
            repository.incrementGoalProgress(goalId, 1, DateUtils.getTodayDateString())
        }
    }

    fun setGlobalNotifications(enabled: Boolean) {
        prefsManager.isGlobalNotificationsEnabled = enabled
        _globalNotifications.value = enabled
        if (!enabled) {
            // Cancel all alarms if global notifications disabled
            viewModelScope.launch {
                allGoals.value.forEach { alarmScheduler.cancelGoalReminder(it.id) }
            }
        } else {
            viewModelScope.launch {
                repository.rescheduleAllActiveAlarms()
            }
        }
    }

    fun setSnoozeDuration(minutes: Int) {
        prefsManager.snoozeDurationMinutes = minutes
        _snoozeDuration.value = minutes
    }

    fun setThemeMode(mode: String) {
        prefsManager.themeMode = mode
        _themeMode.value = mode
    }

    fun resetAllData() {
        viewModelScope.launch {
            allGoals.value.forEach { alarmScheduler.cancelGoalReminder(it.id) }
            db.goalCompletionDao().deleteAllCompletions()
            db.goalDao().getAllGoals().collect { list ->
                list.forEach { db.goalDao().deleteGoal(it) }
            }
        }
    }

    fun restoreSampleGoals() {
        viewModelScope.launch {
            AppDatabase.populateInitialGoals(db.goalDao())
            repository.rescheduleAllActiveAlarms()
        }
    }
}
