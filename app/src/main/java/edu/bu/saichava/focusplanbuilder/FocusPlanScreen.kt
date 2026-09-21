package edu.bu.saichava.focusplanbuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.bu.saichava.focusplanbuilder.ui.theme.Focus_Plan_BuilderTheme

object FocusPlanTestTags {
    const val SUBJECT_FIELD = "subject_field"
    const val MINUTES_FIELD = "minutes_field"
    const val CREATE_PLAN_BUTTON = "create_plan_button"
    const val RESULT_CARD = "result_card"
}

@Composable
fun FocusPlanScreen(
    subject: String,
    minutesText: String,
    plan: FocusPlan?,
    onSubjectChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    canCreatePlan: Boolean,
    onCreatePlan: () -> Unit,
    modifier: Modifier = Modifier,
    subjectError: Boolean = false,
    minutesError: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.screen_instructions),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = onSubjectChange,
            label = { Text(stringResource(R.string.subject_label)) },
            supportingText = {
                Text(
                    stringResource(
                        if (subjectError) R.string.subject_error else R.string.subject_supporting
                    )
                )
            },
            isError = subjectError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(FocusPlanTestTags.SUBJECT_FIELD)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = minutesText,
            onValueChange = onMinutesChange,
            label = { Text(stringResource(R.string.minutes_label)) },
            supportingText = {
                Text(
                    stringResource(
                        if (minutesError) R.string.minutes_error else R.string.minutes_supporting
                    )
                )
            },
            isError = minutesError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(FocusPlanTestTags.MINUTES_FIELD)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                onCreatePlan()
            },
            enabled = canCreatePlan,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(FocusPlanTestTags.CREATE_PLAN_BUTTON)
        ) {
            Text(stringResource(R.string.create_plan))
        }

        if (plan != null) {
            Spacer(modifier = Modifier.height(24.dp))

            FocusPlanCard(
                plan = plan,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(FocusPlanTestTags.RESULT_CARD)
            )
        }
    }
}

@Composable
private fun FocusPlanCard(
    plan: FocusPlan,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = plan.subject,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            PlanDetailRow(
                label = stringResource(R.string.plan_duration_label),
                value = stringResource(R.string.plan_minutes_value, plan.minutes)
            )
            PlanDetailRow(
                label = stringResource(R.string.plan_category_label),
                value = plan.category
            )
            PlanDetailRow(
                label = stringResource(R.string.plan_break_label),
                value = stringResource(R.string.plan_minutes_value, plan.breakMinutes)
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(
                    R.string.plan_summary,
                    plan.subject,
                    plan.minutes,
                    plan.breakMinutes
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PlanDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FocusPlanScreenEmptyPreview() {
    Focus_Plan_BuilderTheme {
        FocusPlanScreen(
            subject = "",
            minutesText = "",
            plan = null,
            onSubjectChange = {},
            onMinutesChange = {},
            canCreatePlan = false,
            onCreatePlan = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FocusPlanScreenWithPlanPreview() {
    Focus_Plan_BuilderTheme {
        FocusPlanScreen(
            subject = "Compose State",
            minutesText = "45",
            plan = FocusPlan(
                subject = "Compose State",
                minutes = 45,
                category = "Focused session",
                breakMinutes = 10
            ),
            onSubjectChange = {},
            onMinutesChange = {},
            canCreatePlan = true,
            onCreatePlan = {}
        )
    }
}
