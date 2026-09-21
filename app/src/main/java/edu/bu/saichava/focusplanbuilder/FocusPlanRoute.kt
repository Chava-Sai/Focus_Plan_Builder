package edu.bu.saichava.focusplanbuilder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun FocusPlanRoute(
    modifier: Modifier = Modifier
) {
    var subject by rememberSaveable {
        mutableStateOf("")
    }

    var minutesText by rememberSaveable {
        mutableStateOf("")
    }

    var plan by rememberSaveable(stateSaver = FocusPlanSaver) {
        mutableStateOf<FocusPlan?>(null)
    }

    val minutes: Int? = minutesText.toIntOrNull()

    val canCreatePlan =
        subject.isNotBlank() &&
            minutes != null &&
            minutes in MIN_MINUTES..MAX_MINUTES

    val subjectError = subject.isNotEmpty() && subject.isBlank()
    val minutesError = minutesText.isNotEmpty() &&
        (minutes == null || minutes !in MIN_MINUTES..MAX_MINUTES)

    FocusPlanScreen(
        subject = subject,
        minutesText = minutesText,
        plan = plan,
        onSubjectChange = { newSubject ->
            subject = newSubject
            plan = null
        },
        onMinutesChange = { newMinutesText ->
            minutesText = newMinutesText
            plan = null
        },
        canCreatePlan = canCreatePlan,
        onCreatePlan = {
            if (canCreatePlan) {
                plan = FocusPlan(
                    subject = subject.trim(),
                    minutes = minutes,
                    category = durationCategory(minutes),
                    breakMinutes = recommendedBreak(minutes)
                )
            }
        },
        subjectError = subjectError,
        minutesError = minutesError,
        modifier = modifier
    )
}

private val FocusPlanSaver: Saver<FocusPlan?, Any> = listSaver(
    save = { plan ->
        if (plan == null) {
            emptyList()
        } else {
            listOf(plan.subject, plan.minutes, plan.category, plan.breakMinutes)
        }
    },
    restore = { saved ->
        if (saved.isEmpty()) {
            null
        } else {
            FocusPlan(
                subject = saved[0] as String,
                minutes = saved[1] as Int,
                category = saved[2] as String,
                breakMinutes = saved[3] as Int
            )
        }
    }
)
