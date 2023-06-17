package app.ministrylogbook.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ministrylogbook.data.SettingsService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(settingsDataStore: SettingsService) : ViewModel() {

    val name = settingsDataStore.name.stateIn(
        scope = viewModelScope,
        initialValue = "",
        started = SharingStarted.WhileSubscribed(DEFAULT_TIMEOUT)
    )
}

private const val DEFAULT_TIMEOUT = 5000L
