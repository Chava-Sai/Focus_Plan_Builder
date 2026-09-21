package edu.bu.saichava.focusplanbuilder

import org.junit.Assert.assertEquals
import org.junit.Test

class FocusPlanCalculationsTest {
    @Test
    fun durationCategory_belowTen_isInvalid() {
        assertEquals("Invalid", durationCategory(9))
        assertEquals("Invalid", durationCategory(0))
        assertEquals("Invalid", durationCategory(-5))
    }

    @Test
    fun durationCategory_tenThroughTwentyNine_isQuickReview() {
        assertEquals("Quick review", durationCategory(10))
        assertEquals("Quick review", durationCategory(20))
        assertEquals("Quick review", durationCategory(29))
    }

    @Test
    fun durationCategory_thirtyThroughSixty_isFocusedSession() {
        assertEquals("Focused session", durationCategory(30))
        assertEquals("Focused session", durationCategory(45))
        assertEquals("Focused session", durationCategory(60))
    }

    @Test
    fun durationCategory_aboveSixty_isExtendedSession() {
        assertEquals("Extended session", durationCategory(61))
        assertEquals("Extended session", durationCategory(90))
        assertEquals("Extended session", durationCategory(180))
    }

    @Test
    fun recommendedBreak_tenThroughTwentyNine_isFiveMinutes() {
        assertEquals(5, recommendedBreak(10))
        assertEquals(5, recommendedBreak(29))
    }

    @Test
    fun recommendedBreak_thirtyThroughSixty_isTenMinutes() {
        assertEquals(10, recommendedBreak(30))
        assertEquals(10, recommendedBreak(45))
        assertEquals(10, recommendedBreak(60))
    }

    @Test
    fun recommendedBreak_aboveSixty_isFifteenMinutes() {
        assertEquals(15, recommendedBreak(61))
        assertEquals(15, recommendedBreak(180))
    }

    @Test
    fun recommendedBreak_belowTen_isZero() {
        assertEquals(0, recommendedBreak(9))
    }

    @Test
    fun focusPlan_combinesBothCalculations() {
        val plan = FocusPlan(
            subject = "Compose State",
            minutes = 45,
            category = durationCategory(45),
            breakMinutes = recommendedBreak(45)
        )
        assertEquals(
            FocusPlan("Compose State", 45, "Focused session", 10),
            plan
        )
    }
}
