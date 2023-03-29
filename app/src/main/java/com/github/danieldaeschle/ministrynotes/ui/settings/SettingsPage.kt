package com.github.danieldaeschle.ministrynotes.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.danieldaeschle.ministrynotes.R
import com.github.danieldaeschle.ministrynotes.data.Role
import com.github.danieldaeschle.ministrynotes.data.rememberSettingsDataStore
import com.github.danieldaeschle.ministrynotes.lib.AlertDialog
import com.github.danieldaeschle.ministrynotes.lib.condition
import com.github.danieldaeschle.ministrynotes.ui.AppGraph
import com.github.danieldaeschle.ministrynotes.ui.LocalAppNavController
import com.github.danieldaeschle.ministrynotes.ui.shared.Toolbar
import com.github.danieldaeschle.ministrynotes.ui.shared.ToolbarAction
import com.github.danieldaeschle.ministrynotes.ui.theme.MinistryNotesTheme
import kotlinx.coroutines.launch

sealed class SettingsGraph(val route: String) {
    object Root : SettingsGraph("root")
}

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation(route = AppGraph.Settings.route, startDestination = SettingsGraph.Root.route) {
        composable(SettingsGraph.Root.route) {
            SettingsPage()
        }
    }
}

@Composable
fun SettingsPage() {
    val coroutineScope = rememberCoroutineScope()
    val settingsDataStore = rememberSettingsDataStore()

    BaseSettingsPage {
        Section {
            Title("Personal Information")
            Setting(title = "Name", onClick = {}) {
                Text(
                    "Your Name",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }

            RoleSetting()
            GoalSetting()
        }
    }
}

@Composable
fun RoleSetting() {
    var isRoleDialogOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val settingsDataStore = rememberSettingsDataStore()
    val role = settingsDataStore.role.collectAsState(Role.Publisher)

    val handleClose = {
        isRoleDialogOpen = false
    }

    AlertDialog(isOpen = isRoleDialogOpen, onClose = handleClose, title = {
        Text("Role")
    }, negativeButton = {
        TextButton(onClick = handleClose) {
            Text("Cancel")
        }
    }) {
        Role.values().map { role ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            settingsDataStore.setRole(role)
                        }
                        handleClose()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)) {
                Text(role.translate())
            }
        }
    }

    Setting(
        title = "Role",
        onClick = { isRoleDialogOpen = true },
    ) {
        Text(
            role.value.translate(),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

@Composable
fun GoalSetting() {
    var isGoalDialogOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val settingsDataStore = rememberSettingsDataStore()
    val goal = settingsDataStore.goal.collectAsState(null)
    val manuallySetGoal = settingsDataStore.manuallySetGoal.collectAsState(null)

    val goalText = if (goal.value != null) {
        val prefix = if (goal.value != manuallySetGoal.value && manuallySetGoal.value != null) {
            "Manually set: "
        } else {
            ""
        }
        "${prefix}${goal.value} hours"
    } else {
        "No goal set"
    }

    val handleClose = {
        isGoalDialogOpen = false
    }

    AlertDialog(isOpen = isGoalDialogOpen, onClose = handleClose, title = {
        Text("Role")
    }, negativeButton = {
        TextButton(onClick = handleClose) {
            Text("Cancel")
        }
    }) {
        Role.values().map { role ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            settingsDataStore.setRole(role)
                        }
                        handleClose()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)) {
                Text(role.translate())
            }
        }
    }

    Setting(title = "Goal", onClick = { }) {
        Text(
            goalText,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

@Composable
fun BaseSettingsPage(content: @Composable () -> Unit = {}) {
    val navController = LocalAppNavController.current

    val handleBack: () -> Unit = {
        navController.popBackStack()
    }

    MinistryNotesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box {
                Toolbar(padding = PaddingValues(horizontal = 12.dp)) {
                    ToolbarAction(onClick = handleBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Settings", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }
                Column(Modifier.statusBarsPadding()) {
                    Spacer(Modifier.height(56.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun Section(content: @Composable () -> Unit) {
    Column(Modifier.padding(vertical = 20.dp)) {
        content()
    }
}

@Composable
fun Title(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, start = 20.dp, end = 20.dp),
    )
}

@Composable
fun Setting(
    title: String,
    icon: Painter? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    value: @Composable () -> Unit = {}
) {
    Row(
        Modifier
            .condition(onClick != null) {
                clickable(onClick = onClick!!)
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(20.dp))
        }
        Column {
            Text(title, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            description?.let {
                Text(
                    description,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        value()
    }
}