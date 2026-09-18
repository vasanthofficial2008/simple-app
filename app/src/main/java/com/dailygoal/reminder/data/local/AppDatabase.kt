package com.dailygoal.reminder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailygoal.reminder.data.local.dao.GoalCompletionDao
import com.dailygoal.reminder.data.local.dao.GoalDao
import com.dailygoal.reminder.data.local.entity.GoalCompletionEntity
import com.dailygoal.reminder.data.local.entity.GoalEntity
import com.dailygoal.reminder.data.model.GoalCategory
import com.dailygoal.reminder.data.model.RepeatSchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [GoalEntity::class, GoalCompletionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun goalCompletionDao(): GoalCompletionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_goal_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialGoals(database.goalDao())
                    }
                }
            }
        }

        suspend fun populateInitialGoals(goalDao: GoalDao) {
            val sampleGoals = listOf(
                GoalEntity(
                    title = "Drink 2L Water",
                    description = "Stay hydrated with 2 liters of fresh water daily",
                    category = GoalCategory.WATER.name,
                    targetCount = 2,
                    unit = "Liters",
                    reminderHour = 8,
                    reminderMinute = 0,
                    isReminderEnabled = true,
                    repeatDaysMask = RepeatSchedule.EVERYDAY,
                    colorHex = GoalCategory.WATER.colorHex,
                    iconName = "LocalDrinkingWater"
                ),
                GoalEntity(
                    title = "Exercise for 30 mins",
                    description = "Cardio, gym, or a brisk walk outdoors",
                    category = GoalCategory.EXERCISE.name,
                    targetCount = 30,
                    unit = "Minutes",
                    reminderHour = 17,
                    reminderMinute = 30,
                    isReminderEnabled = true,
                    repeatDaysMask = RepeatSchedule.EVERYDAY,
                    colorHex = GoalCategory.EXERCISE.colorHex,
                    iconName = "FitnessCenter"
                ),
                GoalEntity(
                    title = "Read 10 Pages",
                    description = "Read a book, article, or educational material",
                    category = GoalCategory.READING.name,
                    targetCount = 10,
                    unit = "Pages",
                    reminderHour = 21,
                    reminderMinute = 0,
                    isReminderEnabled = true,
                    repeatDaysMask = RepeatSchedule.EVERYDAY,
                    colorHex = GoalCategory.READING.colorHex,
                    iconName = "MenuBook"
                ),
                GoalEntity(
                    title = "Study for 1 Hour",
                    description = "Focus on learning new skills or deep work",
                    category = GoalCategory.STUDY.name,
                    targetCount = 1,
                    unit = "Hours",
                    reminderHour = 19,
                    reminderMinute = 0,
                    isReminderEnabled = true,
                    repeatDaysMask = RepeatSchedule.WEEKDAYS,
                    colorHex = GoalCategory.STUDY.colorHex,
                    iconName = "School"
                ),
                GoalEntity(
                    title = "Take Medicine",
                    description = "Daily prescribed vitamins and supplements",
                    category = GoalCategory.MEDICINE.name,
                    targetCount = 1,
                    unit = "Doses",
                    reminderHour = 9,
                    reminderMinute = 0,
                    isReminderEnabled = true,
                    repeatDaysMask = RepeatSchedule.EVERYDAY,
                    colorHex = GoalCategory.MEDICINE.colorHex,
                    iconName = "Medication"
                )
            )
            goalDao.insertGoals(sampleGoals)
        }
    }
}
