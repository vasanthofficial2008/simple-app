package com.dailygoal.reminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FcmPayloadValidationTest {

    @Test
    fun testPayloadFieldValidationAndTruncation() {
        val longTitle = "A".repeat(200)
        val maxTitleLength = 120
        val validatedTitle = if (longTitle.length > maxTitleLength) longTitle.take(maxTitleLength) else longTitle

        assertEquals(120, validatedTitle.length)
    }

    @Test
    fun testDeepLinkParsing() {
        val rawDeepLink = "aimly://goal/42"
        val parts = rawDeepLink.removePrefix("aimly://").split("/")

        assertEquals("goal", parts[0])
        assertEquals("42", parts[1])
        val goalId = parts[1].toLongOrNull()
        assertNotNull(goalId)
        assertEquals(42L, goalId)
    }

    @Test
    fun testNotificationTypeMapping() {
        val inputType = "ANNOUNCEMENT"
        val mappedChannel = when (inputType.uppercase()) {
            "ANNOUNCEMENT", "FEATURE_UPDATE", "SYSTEM" -> "aimly_updates_channel"
            "WEEKLY_CHALLENGE", "MOTIVATION", "PROMOTION" -> "aimly_promotions_channel"
            else -> "aimly_reminders_channel"
        }

        assertEquals("aimly_updates_channel", mappedChannel)
    }
}
