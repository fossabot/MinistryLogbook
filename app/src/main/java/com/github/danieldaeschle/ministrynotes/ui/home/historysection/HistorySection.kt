package com.github.danieldaeschle.ministrynotes.ui.home.historysection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.danieldaeschle.ministrynotes.R
import com.github.danieldaeschle.ministrynotes.data.Entry
import com.github.danieldaeschle.ministrynotes.ui.LocalAppNavController
import com.github.danieldaeschle.ministrynotes.ui.home.HomeGraph
import com.github.danieldaeschle.ministrynotes.ui.home.viewmodels.HomeViewModel
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun HistorySection(homeViewModel: HomeViewModel = koinViewModel()) {
    val navController = LocalAppNavController.current
    val entries = homeViewModel.entries.collectAsState()
    val orderedEntries = entries.value.sortedBy { it.datetime }.reversed()

    val handleClick: (entry: Entry) -> Unit = {
        navController.navigate(HomeGraph.EntryDetails.createRoute(it.id))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        orderedEntries.forEach {
            HistoryItem(it, onClick = { handleClick(it) })
        }
    }
}

@Composable
fun HistoryItem(entry: Entry, onClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("E, dd. MMMM")
    val dateText = formatter.format(entry.datetime.toJavaLocalDate())

    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(entry.kind.color().copy(0.2f))
                .padding(8.dp)
        ) {
            Icon(
                painter = entry.kind.icon(),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = entry.kind.color().copy(0.8f),
            )
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(entry.kind.translate(), modifier = Modifier.padding(bottom = 4.dp))

                Text(
                    dateText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                )
            }

            Row {
                if (entry.hours > 0 || entry.minutes > 0) {
                    val minutes =
                        if (entry.minutes > 0) ":${
                            entry.minutes.toString().padStart(2, '0')
                        }" else ""
                    HistoryItemChip(
                        icon = painterResource(R.drawable.ic_schedule),
                        text = "${entry.hours}${minutes} hrs"
                    )
                    Spacer(Modifier.width(8.dp))
                }
                if (entry.placements > 0) {
                    HistoryItemChip(
                        icon = painterResource(R.drawable.ic_article),
                        text = entry.placements.toString()
                    )
                    Spacer(Modifier.width(8.dp))
                }
                if (entry.returnVisits > 0) {
                    HistoryItemChip(
                        icon = painterResource(R.drawable.ic_group),
                        text = entry.returnVisits.toString()
                    )
                    Spacer(Modifier.width(8.dp))
                }
                if (entry.videoShowings > 0) {
                    HistoryItemChip(
                        icon = painterResource(R.drawable.ic_play_circle),
                        text = entry.videoShowings.toString()
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryItemChip(icon: Painter? = null, text: String) {
    val color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(0.1f))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
        }
        Text(
            text, style = TextStyle(color = color), modifier = Modifier.padding(start = 4.dp)
        )
    }
}