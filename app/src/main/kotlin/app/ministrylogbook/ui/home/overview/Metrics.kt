package app.ministrylogbook.ui.home.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ministrylogbook.R
import app.ministrylogbook.shared.utilities.condition
import app.ministrylogbook.shared.utilities.ministries
import app.ministrylogbook.shared.utilities.placements
import app.ministrylogbook.shared.utilities.returnVisits
import app.ministrylogbook.shared.utilities.videoShowings
import app.ministrylogbook.ui.LocalAppNavController
import app.ministrylogbook.ui.home.navigateToStudies
import app.ministrylogbook.ui.home.viewmodel.OverviewViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Metrics(homeViewModel: OverviewViewModel = koinViewModel()) {
    val navController = LocalAppNavController.current
    val entries by homeViewModel.entries.collectAsStateWithLifecycle()
    val bibleStudies by homeViewModel.bibleStudies.collectAsStateWithLifecycle()
    val ministries by remember(entries) { derivedStateOf { entries.ministries() } }
    val placements by remember(ministries) { derivedStateOf { ministries.placements() } }
    val returnVisits by remember(ministries) { derivedStateOf { ministries.returnVisits() } }
    val videoShowings by remember(ministries) { derivedStateOf { ministries.videoShowings() } }

    Row(Modifier.padding(horizontal = 10.dp, vertical = 16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Metric(stringResource(R.string.placements_short), placements, icon = {
                Icon(
                    painterResource(R.drawable.ic_article),
                    contentDescription = null, // TODO: contentDescription
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            })
            Spacer(modifier = Modifier.height(16.dp))
            Metric(stringResource(R.string.video_showings_short), videoShowings, icon = {
                Icon(
                    painterResource(R.drawable.ic_play_circle),
                    contentDescription = null, // TODO: contentDescription
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            })
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Metric(stringResource(R.string.return_visits), returnVisits, icon = {
                Icon(
                    painterResource(R.drawable.ic_group),
                    contentDescription = null, // TODO: contentDescription
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            })
            Spacer(modifier = Modifier.height(16.dp))
            Metric(stringResource(R.string.bible_studies_short), bibleStudies, icon = {
                Icon(
                    painterResource(R.drawable.ic_local_library),
                    contentDescription = null, // TODO: contentDescription
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }, onClick = {
                navController.navigateToStudies(
                    homeViewModel.month.year,
                    homeViewModel.month.monthNumber
                )
            })
        }
    }
}

@Composable
fun Metric(
    name: String,
    count: Int,
    icon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.padding(2.dp)) {
            val modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .condition(onClick != null) {
                    clickable(onClick = onClick!!).background(MaterialTheme.colorScheme.onSurface.copy(0.1f))
                }
                .padding(4.dp, 4.dp, 16.dp, 4.dp)

            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(0.2f))
                            .padding(12.dp)
                    ) {
                        icon()
                    }
                    Spacer(Modifier.width(16.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            count.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (onClick != null) {
                            Spacer(Modifier.weight(1f))
                            Icon(
                                painterResource(R.drawable.ic_edit),
                                contentDescription = null, // TODO: contentDescription
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                            )
                        }
                    }

                    Text(
                        name,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
