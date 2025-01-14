package app.ministrylogbook.ui.home.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ministrylogbook.R
import app.ministrylogbook.data.Role
import app.ministrylogbook.shared.Time
import app.ministrylogbook.shared.layouts.progress.LinearProgressIndicator
import app.ministrylogbook.shared.layouts.progress.Progress
import app.ministrylogbook.shared.sum
import app.ministrylogbook.shared.utilities.ministryTimeSum
import app.ministrylogbook.shared.utilities.splitIntoMonths
import app.ministrylogbook.shared.utilities.theocraticAssignmentTimeSum
import app.ministrylogbook.shared.utilities.theocraticSchoolTimeSum
import app.ministrylogbook.ui.home.viewmodel.OverviewViewModel
import app.ministrylogbook.ui.theme.ProgressPositive
import org.koin.androidx.compose.koinViewModel

@Composable
fun YearlyProgress(viewModel: OverviewViewModel = koinViewModel()) {
    val role by viewModel.role.collectAsStateWithLifecycle()
    val beginOfPioneeringInServiceYear by viewModel.beginOfPioneeringInServiceYear.collectAsStateWithLifecycle()
    val show by remember(role, beginOfPioneeringInServiceYear) {
        derivedStateOf {
            val isPioneer = role == Role.SpecialPioneer || role == Role.RegularPioneer
            isPioneer && beginOfPioneeringInServiceYear != null && beginOfPioneeringInServiceYear!! <= viewModel.month
        }
    }

    if (show) {
        Spacer(Modifier.height(16.dp))

        Tile(title = { Text(stringResource(R.string.progress_of_yearly_goal)) }) {
            val yearlyGoal by viewModel.yearlyGoal.collectAsStateWithLifecycle()
            val goal by viewModel.roleGoal.collectAsStateWithLifecycle()
            val maxHoursWithCredit by remember(goal) { derivedStateOf { Time(goal + 5, 0) } }
            val entriesInServiceYear by viewModel.entriesInServiceYear.collectAsStateWithLifecycle()
            val time by remember(entriesInServiceYear, maxHoursWithCredit) {
                derivedStateOf {
                    entriesInServiceYear.splitIntoMonths().map {
                        val ministryTimeSum = it.ministryTimeSum()
                        val theocraticSchoolTimeSum = it.theocraticSchoolTimeSum()
                        val theocraticAssignmentTimeSum = it.theocraticAssignmentTimeSum()
                        val max = maxOf(ministryTimeSum, maxHoursWithCredit)
                        minOf(max, ministryTimeSum + theocraticAssignmentTimeSum) + theocraticSchoolTimeSum
                    }.sum()
                }
            }
            val ministryTime by remember(entriesInServiceYear) {
                derivedStateOf {
                    entriesInServiceYear.ministryTimeSum()
                }
            }
            val remaining by remember(time, yearlyGoal) { derivedStateOf { yearlyGoal - time.hours } }

            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = AnnotatedString(
                        time.hours.toString(),
                        spanStyle = SpanStyle(fontSize = 20.sp)
                    ) + AnnotatedString(" ${stringResource(R.string.of)} ") + AnnotatedString(
                        text = stringResource(R.string.hours_value_short_unit, yearlyGoal),
                        spanStyle = SpanStyle(fontSize = 20.sp)
                    ),
                    color = ProgressPositive,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(16.dp))

                LinearProgressIndicator(
                    progresses = listOf(
                        Progress(percent = (1f / yearlyGoal * time.hours), color = ProgressPositive.copy(alpha = .6f)),
                        Progress(percent = (1f / yearlyGoal * ministryTime.hours), color = ProgressPositive)
                    ),
                    modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth(),
                    strokeCap = StrokeCap.Round
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                val text = if (remaining > 0) {
                    pluralStringResource(R.plurals.hours_remaining, remaining, remaining)
                } else {
                    stringResource(R.string.goal_reached)
                }
                Text(
                    text = text,
                    fontSize = 14.sp
                )
            }
        }
    }
}
