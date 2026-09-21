package edu.bu.saichava.focusplanbuilder

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import edu.bu.saichava.focusplanbuilder.ui.theme.Focus_Plan_BuilderTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusPlanScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun subjectField() = composeTestRule.onNodeWithTag(FocusPlanTestTags.SUBJECT_FIELD)
    private fun minutesField() = composeTestRule.onNodeWithTag(FocusPlanTestTags.MINUTES_FIELD)
    private fun createButton() = composeTestRule.onNodeWithTag(FocusPlanTestTags.CREATE_PLAN_BUTTON)
    private fun resultCard() = composeTestRule.onNodeWithTag(FocusPlanTestTags.RESULT_CARD)

    private fun showRoute() {
        composeTestRule.setContent {
            Focus_Plan_BuilderTheme {
                FocusPlanRoute()
            }
        }
    }

    private fun enter(subject: String, minutes: String) {
        subjectField().performTextReplacement(subject)
        minutesField().performTextReplacement(minutes)
    }

    private fun assertDetail(label: String, value: String) {
        composeTestRule
            .onNode(hasText(value) and hasAnySibling(hasText(label)))
            .assertIsDisplayed()
    }

    private fun assertPlanShown(subject: String, minutes: Int, category: String, breakMinutes: Int) {
        resultCard().assertIsDisplayed()
        composeTestRule
            .onNode(hasText(subject) and hasAnyAncestor(hasTestTag(FocusPlanTestTags.RESULT_CARD)))
            .assertIsDisplayed()
        assertDetail("Duration:", "$minutes minutes")
        assertDetail("Category:", category)
        assertDetail("Recommended break:", "$breakMinutes minutes")
        composeTestRule
            .onNodeWithText("Study $subject for $minutes minutes, and then take a $breakMinutes-minute break.")
            .assertIsDisplayed()
    }

    private fun assertDisabledAndNoCard() {
        createButton().assertIsNotEnabled()
        resultCard().assertDoesNotExist()
    }

    private fun createPlanAndAssert(minutes: Int, category: String, breakMinutes: Int) {
        showRoute()
        enter("Kotlin", minutes.toString())
        createButton().assertIsEnabled()
        resultCard().assertDoesNotExist()
        createButton().performClick()
        assertPlanShown("Kotlin", minutes, category, breakMinutes)
    }

    @Test
    fun blankSubject_25Minutes_buttonDisabled() {
        showRoute()
        enter("", "25")
        assertDisabledAndNoCard()
    }

    @Test
    fun kotlin_blankMinutes_buttonDisabled() {
        showRoute()
        enter("Kotlin", "")
        assertDisabledAndNoCard()
    }

    @Test
    fun kotlin_abcMinutes_buttonDisabled_noCrash() {
        showRoute()
        enter("Kotlin", "abc")
        assertDisabledAndNoCard()
    }

    @Test
    fun kotlin_9Minutes_buttonDisabled() {
        showRoute()
        enter("Kotlin", "9")
        assertDisabledAndNoCard()
    }

    @Test
    fun kotlin_10Minutes_quickReview_5MinuteBreak() =
        createPlanAndAssert(10, "Quick review", 5)

    @Test
    fun kotlin_29Minutes_quickReview_5MinuteBreak() =
        createPlanAndAssert(29, "Quick review", 5)

    @Test
    fun kotlin_30Minutes_focusedSession_10MinuteBreak() =
        createPlanAndAssert(30, "Focused session", 10)

    @Test
    fun kotlin_60Minutes_focusedSession_10MinuteBreak() =
        createPlanAndAssert(60, "Focused session", 10)

    @Test
    fun kotlin_61Minutes_extendedSession_15MinuteBreak() =
        createPlanAndAssert(61, "Extended session", 15)

    @Test
    fun kotlin_180Minutes_extendedSession_15MinuteBreak() =
        createPlanAndAssert(180, "Extended session", 15)

    @Test
    fun kotlin_181Minutes_buttonDisabled() {
        showRoute()
        enter("Kotlin", "181")
        assertDisabledAndNoCard()
    }

    @Test
    fun subjectOnlySpaces_buttonDisabled() {
        showRoute()
        enter("   ", "45")
        assertDisabledAndNoCard()
    }

    @Test
    fun subjectIsTrimmedInResultCard() {
        showRoute()
        enter("  Compose State  ", "45")
        createButton().performClick()
        assertPlanShown("Compose State", 45, "Focused session", 10)
    }

    @Test
    fun buttonEnabledStateFollowsInputAutomatically() {
        showRoute()
        createButton().assertIsNotEnabled()
        subjectField().performTextReplacement("Kotlin")
        createButton().assertIsNotEnabled()
        minutesField().performTextReplacement("45")
        createButton().assertIsEnabled()
        minutesField().performTextReplacement("181")
        createButton().assertIsNotEnabled()
        minutesField().performTextReplacement("180")
        createButton().assertIsEnabled()
        subjectField().performTextClearance()
        createButton().assertIsNotEnabled()
    }

    @Test
    fun erasingMinutesAfterPlan_removesCard_disablesButton_noCrash() {
        showRoute()
        enter("Kotlin", "45")
        createButton().performClick()
        resultCard().assertIsDisplayed()

        minutesField().performTextClearance()

        resultCard().assertDoesNotExist()
        createButton().assertIsNotEnabled()
    }

    @Test
    fun changingSubjectAfterPlan_removesOldCard() {
        showRoute()
        enter("Kotlin", "45")
        createButton().performClick()
        resultCard().assertIsDisplayed()

        subjectField().performTextReplacement("Databases")

        resultCard().assertDoesNotExist()
        createButton().assertIsEnabled()
    }

    @Test
    fun changingMinutesAfterPlan_removesOldCard() {
        showRoute()
        enter("Kotlin", "45")
        createButton().performClick()
        resultCard().assertIsDisplayed()

        minutesField().performTextReplacement("90")

        resultCard().assertDoesNotExist()
        createButton().assertIsEnabled()
    }

    @Test
    fun inputsSurviveStateRestoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            Focus_Plan_BuilderTheme {
                FocusPlanRoute()
            }
        }
        enter("Kotlin", "45")

        restorationTester.emulateSavedInstanceStateRestore()

        subjectField().assertTextContains("Kotlin")
        minutesField().assertTextContains("45")
        createButton().assertIsEnabled()
    }

    @Test
    fun createdPlanSurvivesStateRestoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            Focus_Plan_BuilderTheme {
                FocusPlanRoute()
            }
        }
        enter("Kotlin", "45")
        createButton().performClick()
        resultCard().assertIsDisplayed()

        restorationTester.emulateSavedInstanceStateRestore()

        assertPlanShown("Kotlin", 45, "Focused session", 10)
    }
}
