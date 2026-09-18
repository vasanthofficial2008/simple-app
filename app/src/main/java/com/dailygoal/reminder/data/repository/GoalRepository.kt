package com.dailygoal.reminder.data.local.repository

import com.dailygoal.reminder.data.local.dao.GoalCompletionDao
import com.dailygoal.reminder.data.local.dao.GoalDao
import com.dailygoal.reminder.data.local.entity.GoalCompletionEntity
import com.dailygoal.reminder.data.local.entity.GoalEntity
import com.dailygoal.reminder.data.model.GoalWithProgress
import com.dailygoal.reminder.data.model.StatsSummary
import com.dailygoal.reminder.notification.AlarmScheduler
import com.dailygoal.reminder.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GoalRepository(
    private val goalDao: GoalDao,
    private val goalCompletionDao: GoalCompletionDao,
    private val alarmScheduler: AlarmScheduler
) {
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    val activeGoals: Flow<List<GoalEntity>> = goalDao.getActiveGoals()

    fun getTodayGoalsWithProgress(): Flow<List<GoalWithProgress>> {
        val todayStr = DateUtils.getTodayDateString()
        return combine(
            goalDao.getAllGoals(),
            goalCompletionDao.getCompletionsForDate(todayStr),
            goalCompletionDao.getAllCompletedDates()
        ) { goals, completions, completedDates ->
            val completionMap = completions.associateBy { it.goalId }
            val overallStreak = DateUtils.calculateStreak(completedDates)

            goals.map { goal ->
                val completion = completionMap[goal.id]
                val currentProgress = completion?.completedCount ?: 0
                val isCompleted = completion?.isCompleted ?: (currentProgress >= goal.targetCount)
                GoalWithProgress(
                    goal = goal,
                    currentProgress = currentProgress,
                    isCompletedToday = isCompleted,
                    streak = overallStreak
                )
            }
        }
    }

    fun getStatsSummary(): Flow<StatsSummary> {
        val todayStr = DateUtils.getTodayDateString()
        return combine(
            goalDao.getActiveGoals(),
            goalCompletionDao.getCompletionsForDate(todayStr),
            goalCompletionDao.getAllCompletedDates()
        ) { activeGoals, todayCompletions, allCompletedDates ->
            val totalActive = activeGoals.size
            val completionMap = todayCompletions.associateBy { it.goalId }

            var completedToday = 0
            activeGoals.forEach { goal ->
                val comp = completionMap[goal.id]
                val isDone = comp?.isCompleted ?: ((comp?.completedCount ?: 0) >= goal.targetCount)
                if (isDone) completedToday++
            }

            val remainingToday = (totalActive - completedToday).coerceAtLeast(0)
            val todayPercent = if (totalActive > 0) ((completedToday.toFloat() / totalActive) * 100).toInt() else 0

            val currentStreak = DateUtils.calculateStreak(allCompletedDates)

            // Calculate upcoming reminder
            val nowMin = java.util.Calendar.getInstance().run { get(java.util.Calendar.HOUR_OF_DAY) * 60 + get(java.util.Calendar.MINUTE) }
            val upcomingGoal = activeGoals
                .filter { it.isReminderEnabled }
                .map { goal ->
                    val goalMin = goal.reminderHour * 60 + goal.reminderMinute
                    val diff = if (goalMin >= nowMin) goalMin - nowMin else (24 * 60 - nowMin) + goalMin
                    Pair(goal, diff)
                }
                .minByOrNull { it.second }?.first

            val upcomingTimeFormatted = upcomingGoal?.let {
                DateUtils.formatTime(it.reminderHour, it.reminderMinute)
            }

            StatsSummary(
                totalGoalsCount = totalActive,
                completedTodayCount = completedToday,
                remainingTodayCount = remainingToday,
                todayCompletionPercentage = todayPercent,
                weeklyCompletionPercentage = todayPercent, // Refined by date range in UI
                currentStreak = currentStreak,
                bestStreak = currentStreak, // Can track historical max
                upcomingReminderTime = upcomingTimeFormatted,
                upcomingGoalTitle = upcomingGoal?.title
            )
        }
    }

    fun getGoalById(id: Long): Flow<GoalEntity?> = goalDao.getGoalById(id)

    suspend fun addGoal(goal: GoalEntity): Long {
        val id = goalDao.insertGoal(goal)
        val createdGoal = goal.copy(id = id)
        if (createdGoal.isReminderEnabled && !createdGoal.isPaused) {
            alarmScheduler.scheduleGoalReminder(createdGoal)
        }
        return id
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
        if (goal.isReminderEnabled && !goal.isPaused) {
            alarmScheduler.scheduleGoalReminder(goal)
        } else {
            alarmScheduler.cancelGoalReminder(goal.id)
        }
    }

    suspend fun toggleGoalPause(goal: GoalEntity) {
        val updated = goal.copy(isPaused = !goal.isPaused)
        goalDao.updateGoal(updated)
        if (updated.isPaused || !updated.isReminderEnabled) {
            alarmScheduler.cancelGoalReminder(updated.id)
        } else {
            alarmScheduler.scheduleGoalReminder(updated)
        }
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        alarmScheduler.cancelGoalReminder(goal.id)
        goalCompletionDao.deleteCompletionsForGoal(goal.id)
        goalDao.deleteGoal(goal)
    }

    suspend fun toggleGoalCompletion(goalId: Long, dateString: String = DateUtils.getTodayDateString()) {
        val goal = goalDao.getGoalByIdSync(goalId) ?: return
        val existing = goalCompletionDao.getCompletion(goalId, dateString)

        if (existing == null) {
            val newCompletion = GoalCompletionEntity(
                goalId = goalId,
                dateString = dateString,
                completedCount = goal.targetCount,
                isCompleted = true
            )
            goalCompletionDao.insertCompletion(newCompletion)
        } else {
            val isNowCompleted = !existing.isCompleted
            val newCount = if (isNowCompleted) goal.targetCount else 0
            val updated = existing.copy(
                isCompleted = isNowCompleted,
                completedCount = newCount,
                timestamp = System.currentTimeMillis()
            )
            goalCompletionDao.updateCompletion(updated)
        }
    }

    suspend fun incrementGoalProgress(goalId: Long, incrementBy: Int = 1, dateString: String = DateUtils.getTodayDateString()) {
        val goal = goalDao.getGoalByIdSync(goalId) ?: return
        val existing = goalCompletionDao.getCompletion(goalId, dateString)

        if (existing == null) {
            val newCount = incrementBy.coerceAtMost(goal.targetCount)
            val newCompletion = GoalCompletionEntity(
                goalId = goalId,
                dateString = dateString,
                completedCount = newCount,
                isCompleted = newCount >= goal.targetCount
            )
            goalCompletionDao.insertCompletion(newCompletion)
        } else {
            val newCount = (existing.completedCount + incrementBy).coerceAtMost(goal.targetCount)
            val updated = existing.copy(
                completedCount = newCount,
                isCompleted = newCount >= goal.targetCount,
                timestamp = System.currentTimeMillis()
            )
            goalCompletionDao.updateCompletion(updated)
        }
    }

    suspend fun rescheduleAllActiveAlarms() {
        val activeGoals = goalDao.getActiveGoalsSync()
        activeGoals.forEach { goal ->
            if (goal.isReminderEnabled) {
                alarmScheduler.scheduleGoalReminder(goal)
            }
        }
    }
}
