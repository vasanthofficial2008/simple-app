package com.dailygoal.reminder.data.model

object RepeatSchedule {
    const val MONDAY = 1 shl 0
    const val TUESDAY = 1 shl 1
    const val WEDNESDAY = 1 shl 2
    const val THURSDAY = 1 shl 3
    const val FRIDAY = 1 shl 4
    const val SATURDAY = 1 shl 5
    const val SUNDAY = 1 shl 6

    const val EVERYDAY = MONDAY or TUESDAY or WEDNESDAY or THURSDAY or FRIDAY or SATURDAY or SUNDAY
    const val WEEKDAYS = MONDAY or TUESDAY or WEDNESDAY or THURSDAY or FRIDAY
    const val WEEKENDS = SATURDAY or SUNDAY

    fun getRepeatDaysText(mask: Int): String {
        return when (mask) {
            EVERYDAY -> "Every day"
            WEEKDAYS -> "Weekdays (Mon-Fri)"
            WEEKENDS -> "Weekends (Sat-Sun)"
            0 -> "No repeat"
            else -> {
                val days = mutableListOf<String>()
                if ((mask and MONDAY) != 0) days.add("Mon")
                if ((mask and TUESDAY) != 0) days.add("Tue")
                if ((mask and WEDNESDAY) != 0) days.add("Wed")
                if ((mask and THURSDAY) != 0) days.add("Thu")
                if ((mask and FRIDAY) != 0) days.add("Fri")
                if ((mask and SATURDAY) != 0) days.add("Sat")
                if ((mask and SUNDAY) != 0) days.add("Sun")
                days.joinToString(", ")
            }
        }
    }
}
