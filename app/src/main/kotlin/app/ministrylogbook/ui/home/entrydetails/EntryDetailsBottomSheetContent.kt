package app.ministrylogbook.ui.home.entrydetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ministrylogbook.R
import app.ministrylogbook.data.EntryType
import app.ministrylogbook.shared.layouts.ExpandAnimatedVisibility
import app.ministrylogbook.shared.layouts.OptionList
import app.ministrylogbook.ui.LocalAppNavController
import app.ministrylogbook.ui.home.viewmodel.EntryDetailsViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailsBottomSheetContent(viewModel: EntryDetailsViewModel = koinViewModel()) {
    val navController = LocalAppNavController.current
    val entry by viewModel.entry.collectAsStateWithLifecycle()
    val isInFuture by remember(entry) {
        derivedStateOf {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val firstDayOfNextMonth = LocalDate(today.year, today.month, 1).plus(
                DatePeriod(months = 1)
            )
            // if it's in next month, one can only create entries in the current month or past
            entry.datetime.date >= firstDayOfNextMonth
        }
    }
    val isSavable by remember(entry, isInFuture) {
        derivedStateOf {
            entry.let {
                it.hours > 0 || it.minutes > 0 || it.returnVisits > 0 || it.placements > 0 ||
                    it.videoShowings > 0
            } && !isInFuture
        }
    }
    val hasLoaded = (viewModel.id ?: 0) == entry.id
    var isDateDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isEntryKindDialogVisible by rememberSaveable { mutableStateOf(false) }
    val role by viewModel.role.collectAsStateWithLifecycle()
    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    val isCreditEnabled by remember(role, entry) {
        derivedStateOf { role.canHaveCredit || entry.isCredit }
    }
    val dateMillis by remember(entry) {
        derivedStateOf {
            entry.datetime
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateMillis,
        initialDisplayedMonthMillis = dateMillis
    )
    val datePickerConfirmEnabled by remember(datePickerState) {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    val handleClose: () -> Unit = {
        isDateDialogVisible = false
        navController.navigateUp()
    }
    val handleSave = {
        viewModel.save()
        handleClose()
    }
    val handleDelete = {
        viewModel.delete()
        handleClose()
    }
    val handleChangeHours: (newValue: Int) -> Unit = {
        if (it in 0..99) {
            viewModel.update(hours = it)
        }
    }
    val handleChangeMinutes: (newValue: Int) -> Unit = {
        if (entry.hours > 0 && it < 0) {
            viewModel.update(
                hours = entry.hours - 1,
                minutes = 60 + it
            )
        } else if (it > 59) {
            viewModel.update(
                hours = entry.hours + 1,
                minutes = it - 60
            )
        } else if (it in 0..59) {
            viewModel.update(minutes = it)
        }
    }
    val handleChangePlacements: (newValue: Int) -> Unit = {
        if (it in 0..999) {
            viewModel.update(placements = it)
        }
    }

    val handleChangeReturnVisits: (newValue: Int) -> Unit = {
        if (it in 0..99) {
            viewModel.update(returnVisits = it)
        }
    }
    val handleChangeVideos: (newValue: Int) -> Unit = {
        if (it in 0..99) {
            viewModel.update(videoShowings = it)
        }
    }
    val handleChangeDate: (newValue: LocalDateTime) -> Unit = {
        viewModel.update(datetime = it)
    }
    val handleKindDate: (newValue: EntryType) -> Unit = {
        viewModel.update(type = it)
    }

    if (isDateDialogVisible) {
        val onDismissRequest = {
            isDateDialogVisible = false
        }
        val onDateChange: () -> Unit = {
            datePickerState.selectedDateMillis?.let {
                val dateTime = Instant.fromEpochMilliseconds(it).toLocalDateTime(
                    TimeZone.currentSystemDefault()
                )
                handleChangeDate(dateTime)
            }
            isDateDialogVisible = false
        }
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onDateChange,
                    enabled = datePickerConfirmEnabled
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isDateDialogVisible = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (isDeleteDialogVisible) {
        val onDismissRequest = {
            isDeleteDialogVisible = false
        }
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(stringResource(R.string.delete_entry)) },
            text = { Text(stringResource(R.string.delete_entry_description)) },
            confirmButton = {
                val onClick = {
                    isDeleteDialogVisible = false
                    handleDelete()
                }
                TextButton(onClick = onClick) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                val onClick = {
                    isDeleteDialogVisible = false
                }
                TextButton(onClick = onClick) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    app.ministrylogbook.shared.layouts.AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        isOpen = isEntryKindDialogVisible,
        onDismissRequest = { isEntryKindDialogVisible = false },
        paddingValues = PaddingValues(vertical = 8.dp)
    ) {
        val entryTypes = listOfNotNull(
            EntryType.Ministry,
            if (role.canHaveCredit) EntryType.TheocraticAssignment else null,
            if (role.canHaveCredit) EntryType.TheocraticSchool else null,
            if (!role.canHaveCredit) entry.type else null
        )

        OptionList(bullets = true) {
            entryTypes.forEach {
                Option(it.translate(), selected = it == entry.type, onClick = {
                    handleKindDate(it)
                    isEntryKindDialogVisible = false
                })
            }
        }
    }

    Column() {
        DragLine()
        Toolbar(
            onClose = handleClose,
            onSave = handleSave,
            isSavable = isSavable,
            onDelete = { isDeleteDialogVisible = true },
            isDeletable = entry.id != 0
        )
        Divider()
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
                .navigationBarsPadding()
        ) {
            Column(
                Modifier
                    .clickable { isDateDialogVisible = true }
                    .padding(bottom = 12.dp, start = 20.dp, top = 12.dp, end = 20.dp)
            ) {
                val dateStr = dateTimeFormatter.format(
                    entry.datetime.toJavaLocalDateTime()
                )
                UnitRow(dateStr, icon = painterResource(R.drawable.ic_today))

                ExpandAnimatedVisibility(show = isInFuture) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.date_in_future),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            if (isCreditEnabled) {
                Divider()
                Box(Modifier.clickable { isEntryKindDialogVisible = true }) {
                    Box(Modifier.padding(bottom = 12.dp, start = 20.dp, top = 12.dp, end = 20.dp)) {
                        UnitRow(
                            entry.type.translate(),
                            icon = entry.type.icon()
                        )
                    }
                }
            }
            Divider()
            Column(
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                UnitRow(
                    stringResource(R.string.hours),
                    icon = painterResource(R.drawable.ic_schedule)
                ) {
                    NumberPicker(entry.hours) {
                        handleChangeHours(it)
                    }
                }
                UnitRow(stringResource(R.string.minutes)) {
                    val precisionMode = viewModel.precisionMode.collectAsStateWithLifecycle()
                    val step = if (precisionMode.value) 1 else 5

                    NumberPicker(entry.minutes, step) {
                        handleChangeMinutes(it)
                    }
                }

                if (hasLoaded) {
                    AnimatedVisibility(visible = entry.type == EntryType.Ministry) {
                        Column {
                            UnitRow(
                                stringResource(R.string.placements_short),
                                description = stringResource(R.string.printed_and_electronic),
                                icon = painterResource(R.drawable.ic_article)
                            ) {
                                NumberPicker(entry.placements) {
                                    handleChangePlacements(it)
                                }
                            }
                            UnitRow(
                                stringResource(R.string.video_showings),
                                icon = painterResource(R.drawable.ic_play_circle)
                            ) {
                                NumberPicker(entry.videoShowings) {
                                    handleChangeVideos(it)
                                }
                            }
                            UnitRow(
                                stringResource(R.string.return_visits),
                                icon = painterResource(R.drawable.ic_group)
                            ) {
                                NumberPicker(entry.returnVisits) {
                                    handleChangeReturnVisits(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
