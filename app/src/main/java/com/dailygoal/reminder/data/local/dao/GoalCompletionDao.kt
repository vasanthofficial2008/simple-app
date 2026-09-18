package com.dailygoal.reminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dailygoal.reminder.data.local.entity.GoalCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalCompletionDao {
    @Query("SELECT * FROM goal_completions WHERE dateString = :dateString")
    fun getCompletionsForDate(dateString: String): Flow<List<GoalCompletionEntity>>

    @Query("SELECT * FROM goal_completions WHERE dateString = :dateString")
    suspend fun getCompletionsForDateSync(dateString: String): List<GoalCompletionEntity>

    @Query("SELECT * FROM goal_completions WHERE goalId = :goalId AND dateString = :dateString LIMIT 1")
    suspend fun getCompletion(goalId: Long, dateString: String): GoalCompletionEntity?

    @Query("SELECT * FROM goal_completions WHERE goalId = :goalId ORDER BY dateString DESC")
    fun getCompletionsForGoal(goalId: Long): Flow<List<GoalCompletionEntity>>

    @Query("SELECT DISTINCT dateString FROM goal_completions WHERE isCompleted = 1")
    fun getAllCompletedDates(): Flow<List<String>>

    @Query("SELECT DISTINCT dateString FROM goal_completions WHERE isCompleted = 1")
    suspend fun getAllCompletedDatesSync(): List<String>

    @Query("SELECT COUNT(DISTINCT dateString) FROM goal_completions WHERE isCompleted = 1")
    fun getTotalCompletedDaysCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: GoalCompletionEntity): Long

    @Update
    suspend fun updateCompletion(completion: GoalCompletionEntity)

    @Query("DELETE FROM goal_completions WHERE goalId = :goalId")
    suspend fun deleteCompletionsForGoal(goalId: Long)

    @Query("DELETE FROM goal_completions")
    suspend fun deleteAllCompletions()
}
