package com.dailygoal.reminder

import com.dailygoal.reminder.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateUtilsTest {

    @Test
    fun testDayOfWeekMasking() {
        val mask = 1 shl 0 // Monday
        assertTrue(DateUtils.isDaySelectedInMask(mask, 0))
        assertFalse(DateUtils.isDaySelectedInMask(mask, 1))
    }

    @Test
    fun testStreakCalculationWithConsecutiveDates() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        val today = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val dayBefore = sdf.format(cal.time)

        val completedDates = listOf(today, yesterday, dayBefore)
        val streak = DateUtils.calculateStreak(completedDates)

        assertEquals(3, streak)
    }

    @Test
    fun testStreakCalculationWithBrokenChain() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        cal.add(Calendar.DAY_OF_YEAR, -5)
        val oldDate = sdf.format(cal.time)

        val completedDates = listOf(oldDate)
        val streak = DateUtils.calculateStreak(completedDates)

        assertEquals(0, streak)
    }
}
