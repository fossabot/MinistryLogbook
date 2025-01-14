package app.ministrylogbook.ui.home.entrydetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ministrylogbook.R
import app.ministrylogbook.ui.LocalAppNavController
import app.ministrylogbook.ui.home.viewmodel.StudiesDetailsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StudiesBottomSheetContent(viewModel: StudiesDetailsViewModel = koinViewModel()) {
    val navController = LocalAppNavController.current
    val bibleStudies by viewModel.bibleStudies.collectAsStateWithLifecycle()
    var tempStudies by remember(bibleStudies) { mutableIntStateOf(bibleStudies) }

    val handleClose: () -> Unit = {
        navController.navigateUp()
    }

    val handleSave: () -> Unit = {
        viewModel.save(tempStudies)
        handleClose()
    }

    val handleChange: (newValue: Int) -> Unit = {
        if (it in 0..99) {
            tempStudies = it
        }
    }

    Column {
        DragLine()
        Toolbar(
            onClose = handleClose,
            onSave = handleSave,
            isSavable = true
        )
        Divider()
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {
            UnitRow(
                stringResource(R.string.bible_studies_short),
                description = stringResource(R.string.bible_studies_long),
                icon = painterResource(R.drawable.ic_local_library)
            ) {
                NumberPicker(tempStudies, onChange = handleChange)
            }
        }
    }
}
