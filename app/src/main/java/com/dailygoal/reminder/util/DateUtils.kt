package com.dailygoal.reminder.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    private val shortMonthDayFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    fun formatDateForDisplay(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            if (date != null) displayDateFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun getTodayFormattedHeader(): String {
        return displayDateFormat.format(Date())
    }

    fun getMonthYearFormatted(calendar: Calendar): String {
        return monthYearFormat.format(calendar.time)
    }

    fun getDayOfWeekBitmask(calendar: Calendar = Calendar.getInstance()): Int {
        // Calendar.SUNDAY = 1, MONDAY = 2, ... SATURDAY = 7
        // We map Monday -> 1<<0, Tuesday -> 1<<1, ... Sunday -> 1<<6
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.MONDAY -> 1 shl 0
            Calendar.TUESDAY -> 1 shl 1
            Calendar.WEDNESDAY -> 1 shl 2
            Calendar.THURSDAY -> 1 shl 3
            Calendar.FRIDAY -> 1 shl 4
            Calendar.SATURDAY -> 1 shl 5
            Calendar.SUNDAY -> 1 shl 6
            else -> 0
        }
    }

    fun isDaySelectedInMask(mask: Int, dayIndex: Int): Boolean {
        // dayIndex: 0 = Mon, 1 = Tue, ..., 6 = Sun
        return (mask and (1 shl dayIndex)) != 0
    }

    fun calculateStreak(completedDates: List<String>): Int {
        if (completedDates.isEmpty()) return 0
        val sortedDates = completedDates.distinct().sortedDescending()

        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(cal.time)

        // Check if user completed today or yesterday to maintain current streak
        if (!sortedDates.contains(todayStr) && !sortedDates.contains(yesterdayStr)) {
            return 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!sortedDates.contains(todayStr)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1) // start checking from yesterday
        }

        while (true) {
            val dateStr = dateFormat.format(checkCal.time)
            if (sortedDates.contains(dateStr)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    fun getPastDays(daysCount: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until daysCount) {
            list.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return list.reversed()
    }

    fun formatTime(hour: Int, minute: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(cal.time)
    }
}
