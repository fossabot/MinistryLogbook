package com.github.danieldaeschle.ministrynotes.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.github.danieldaeschle.ministrynotes.BuildConfig
import com.github.danieldaeschle.ministrynotes.R
import com.github.danieldaeschle.ministrynotes.data.Design
import com.github.danieldaeschle.ministrynotes.data.PublisherGoal
import com.github.danieldaeschle.ministrynotes.data.Role
import com.github.danieldaeschle.ministrynotes.data.rememberSettingsDataStore
import com.github.danieldaeschle.ministrynotes.lib.AlertDialog
import com.github.danieldaeschle.ministrynotes.ui.LocalAppNavController
import com.github.danieldaeschle.ministrynotes.ui.home.OptionList
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsPage() {
    BaseSettingsPage(stringResource(R.string.settings)) {
        Column {
            Title(stringResource(R.string.personal_information))
            NameSetting()
            RoleSetting()
            GoalSetting()
        }
        Column {
            Title(stringResource(R.string.appearance))
            LanguageSetting()
            DesignSetting()
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(
                    R.string.version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun DesignSetting() {
    var isDialogOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val settingsDataStore = rememberSettingsDataStore()
    val design by settingsDataStore.design.collectAsState(Design.System)

    val handleClose = {
        isDialogOpen = false
    }

    AlertDialog(isOpen = isDialogOpen, onClose = handleClose, title = {
        Text(stringResource(R.string.design))
    }, dismissButton = {
        TextButton(onClick = handleClose) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        OptionList {
            Design.values().map { d ->
                Option(text = d.translate(), selected = d == design, onClick = {
                    coroutineScope.launch {
                        d.apply()
                        settingsDataStore.setDesign(d)
                    }
                    handleClose()
                })
            }
        }
    }

    Setting(
        title = stringResource(R.string.design),
        onClick = { isDialogOpen = true },
    ) {
        Text(
            design.translate(),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

@Composable
fun NameSetting() {
    val settingsDataStore = rememberSettingsDataStore()
    val navController = LocalAppNavController.current
    val name by settingsDataStore.name.collectAsState("")
    val noNameSetText = stringResource(R.string.no_name_set)
    val nameOrDefault by remember { derivedStateOf { name.ifEmpty { noNameSetText } } }

    Setting(title = stringResource(R.string.name), onClick = {
        navController.navigateToSettingsName()
    }) {
        Text(
            nameOrDefault,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

@Composable
fun RoleSetting() {
    var isRoleDialogOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val settingsDataStore = rememberSettingsDataStore()
    val role by settingsDataStore.role.collectAsState(Role.Publisher)

    val handleClose = {
        isRoleDialogOpen = false
    }

    AlertDialog(isOpen = isRoleDialogOpen, onClose = handleClose, title = {
        Text(stringResource(R.string.role))
    }, dismissButton = {
        TextButton(onClick = handleClose) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        OptionList {
            Role.values().map { r ->
                Option(text = r.translate(), selected = r == role, onClick = {
                    coroutineScope.launch {
                        settingsDataStore.setRole(r)
                    }
                    handleClose()
                })
            }
        }
    }

    Setting(
        title = stringResource(R.string.role),
        onClick = { isRoleDialogOpen = true },
    ) {
        Text(
            role.translate(),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

@Composable
fun GoalSetting() {
    val navController = LocalAppNavController.current
    val settingsDataStore = rememberSettingsDataStore()
    val role by settingsDataStore.role.collectAsState(null)
    val goal by settingsDataStore.goal.collectAsState(null)
    val roleGoal by settingsDataStore.roleGoal.collectAsState(PublisherGoal)
    val manuallySetGoal by settingsDataStore.manuallySetGoal.collectAsState(null)
    val noGoalSetText = stringResource(R.string.no_goal_set)
    val manuallySetText = stringResource(R.string.manually_set_colon)
    val goalUnitText = pluralStringResource(R.plurals.hours_unit, goal ?: 0, goal ?: 0)

    val goalText by remember(role, goal, roleGoal, manuallySetGoal) {
        derivedStateOf {
            if (role == Role.Publisher && goal == roleGoal && manuallySetGoal == null) {
                noGoalSetText
            } else {
                val showManuallySet =
                    manuallySetGoal != null && manuallySetGoal != roleGoal && role != Role.Publisher
                val prefix = if (showManuallySet) "$manuallySetText " else ""
                goal?.let { prefix + goalUnitText } ?: ""
            }
        }
    }

    Setting(title = stringResource(R.string.goal), onClick = {
        navController.navigateToSettingsGoal()
    }) {
        Text(
            goalText,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

@Composable
fun LanguageSetting() {
    var isDialogOpen by remember { mutableStateOf(false) }
    val locale = AppCompatDelegate.getApplicationLocales().get(0)
    val localeDisplayName = if (locale != null) "${locale.getDisplayLanguage(locale)} (${
        locale.getDisplayLanguage(
            Locale.ENGLISH
        )
    })" else stringResource(R.string.system_default)

    val handleClose = {
        isDialogOpen = false
    }

    AlertDialog(isOpen = isDialogOpen, onClose = handleClose, title = {
        Text(stringResource(R.string.role))
    }, dismissButton = {
        TextButton(onClick = handleClose) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        OptionList {
            supportedLocales.map { supportedLocale ->
                val supportedLocaleDisplayName = if (supportedLocale != null) {
                    "${supportedLocale.getDisplayLanguage(supportedLocale)} (${
                        supportedLocale.getDisplayLanguage(Locale.ENGLISH)
                    })"
                } else {
                    stringResource(R.string.system_default)
                }

                Option(supportedLocaleDisplayName, onClick = {
                    val localeList =
                        if (supportedLocale != null) LocaleListCompat.create(supportedLocale)
                        else LocaleListCompat.getEmptyLocaleList()
                    AppCompatDelegate.setApplicationLocales(localeList)
                    handleClose()
                })
            }
        }
    }

    Setting(title = stringResource(R.string.language), onClick = { isDialogOpen = true }) {
        Text(
            localeDisplayName,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
        )
    }
}

private val supportedLocales = listOf(null, Locale.ENGLISH, Locale.GERMAN)