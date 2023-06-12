package app.ministrylogbook.ui.home.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ministrylogbook.R
import app.ministrylogbook.shared.Time
import app.ministrylogbook.shared.ministryTimeSum
import app.ministrylogbook.shared.progress.CircleProgressIndicator
import app.ministrylogbook.shared.progress.Progress
import app.ministrylogbook.shared.theocraticAssignmentTimeSum
import app.ministrylogbook.shared.theocraticSchoolTimeSum
import app.ministrylogbook.shared.timeSum
import app.ministrylogbook.ui.home.viewmodel.OverviewViewModel
import app.ministrylogbook.ui.theme.ProgressPositive
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsSection(homeViewModel: OverviewViewModel = koinViewModel()) {
    val entries by homeViewModel.entries.collectAsStateWithLifecycle()
    val role by homeViewModel.role.collectAsStateWithLifecycle()
    val goal by homeViewModel.goal.collectAsStateWithLifecycle()
    // credit will be added until goal + 5 hours are reached
    // example: goal = 50, credit = 55
    val maxHoursWithCredit = remember(goal) { Time(goal + 5, 0) }
    val transferred by homeViewModel.transferred.collectAsStateWithLifecycle()
    val transferredTime = remember(transferred) { transferred.timeSum() }
    val ministryTime = remember(entries, transferredTime) { entries.ministryTimeSum() - transferredTime }
    val theocraticAssignmentsTime = remember(entries) { entries.theocraticAssignmentTimeSum() }
    val theocraticSchoolTime = remember(entries) { entries.theocraticSchoolTimeSum() }
    val credit = remember(
        ministryTime,
        theocraticAssignmentsTime,
        maxHoursWithCredit,
        theocraticSchoolTime
    ) { minOf(theocraticAssignmentsTime, maxHoursWithCredit - ministryTime) + theocraticSchoolTime }
    val accumulatedTime = remember(
        ministryTime,
        theocraticAssignmentsTime,
        maxHoursWithCredit,
        theocraticSchoolTime
    ) {
        ministryTime.let {
            if (it.hours < maxHoursWithCredit.hours) {
                minOf(
                    ministryTime + theocraticAssignmentsTime,
                    maxHoursWithCredit
                )
            } else {
                it
            }
        } + theocraticSchoolTime
    }

    Column {
        BoxWithConstraints {
            val widthDp = LocalDensity.current.run {
                min(constraints.maxWidth.toDp(), LocalConfiguration.current.screenHeightDp.dp)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(widthDp / 2)
                        .width(widthDp / 2)
                ) {
                    CircleProgressIndicator(
                        modifier = Modifier.size(widthDp / 2, widthDp / 2),
                        baseLineColor = ProgressPositive.copy(0.15f),
                        progresses = listOfNotNull(
                            Progress(
                                percent = (1f / goal * accumulatedTime.hours),
                                color = ProgressPositive.copy(0.6f)
                            ).takeIf { role.canHaveCredit && credit.isNotEmpty },
                            Progress(
                                percent = (1f / goal * ministryTime.hours),
                                color = ProgressPositive
                            ).takeIf { ministryTime.isNotEmpty }
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Counter(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            time = ministryTime
                        )

                        AnimatedVisibility(
                            visible = role.canHaveCredit && credit > Time(0, 0),
                            enter = expandVertically(
                                tween(
                                    durationMillis = 200,
                                    delayMillis = 600
                                )
                            ) + fadeIn(tween(delayMillis = 850)),
                            exit = shrinkVertically(
                                tween(
                                    durationMillis = 200,
                                    delayMillis = AnimationConstants.DefaultDurationMillis + 50
                                )
                            ) + fadeOut(tween())
                        ) {
                            Row(
                                Modifier
                                    .clip(CircleShape)
                                    .defaultMinSize(minWidth = 48.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val creditMinutes =
                                    if (credit.minutes > 0) {
                                        ":${
                                            credit.minutes.toString().padStart(2, '0')
                                        }"
                                    } else {
                                        ""
                                    }

                                Icon(
                                    painterResource(R.drawable.ic_volunteer_activism),
                                    contentDescription = null, // TODO: contentDescription
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                val text = stringResource(
                                    R.string.hours_short_unit,
                                    "${credit.hours}$creditMinutes"
                                )
                                Text(
                                    text,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Metrics()
    }
}
