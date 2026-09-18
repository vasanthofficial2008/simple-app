package com.dailygoal.reminder.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    
    private fun getIsoDateFormat(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun getDisplayDateFormat(): SimpleDateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    private fun getMonthYearFormat(): SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun getTodayDateString(): String {
        return getIsoDateFormat().format(Date())
    }

    fun formatDateForDisplay(dateString: String): String {
        return try {
            val date = getIsoDateFormat().parse(dateString)
            if (date != null) getDisplayDateFormat().format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun getTodayFormattedHeader(): String {
        return getDisplayDateFormat().format(Date())
    }

    fun getMonthYearFormatted(calendar: Calendar): String {
        return getMonthYearFormat().format(calendar.time)
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
        val isoFormat = getIsoDateFormat()

        val cal = Calendar.getInstance()
        val todayStr = isoFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = isoFormat.format(cal.time)

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
            val dateStr = isoFormat.format(checkCal.time)
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
        val isoFormat = getIsoDateFormat()
        for (i in 0 until daysCount) {
            list.add(isoFormat.format(cal.time))
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
