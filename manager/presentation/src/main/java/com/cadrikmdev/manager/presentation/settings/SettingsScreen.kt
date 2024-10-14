package com.cadrikmdev.manager.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cadrikmdev.core.presentation.designsystem.SignalTrackerManagerTheme
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerScaffold
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerToolbar
import com.cadrikmdev.manager.presentation.R
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference

@Composable
fun SettingsScreenRoot(
    onBackClick: () -> Unit,
) {
    SettingsScreen(
        onBackClick,
        onAction = { },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAction: (SettingsAction) -> Unit,
) {

    SignalTrackerManagerTheme {
        SignalTrackerManagerScaffold(
            topAppBar = {
                SignalTrackerManagerToolbar(
                    showBackButton = true,
                    title = stringResource(id = R.string.settings),
                    onBackClick = onBackClick
                )
            },
        ) { padding ->
            ProvidePreferenceLocals {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    preferenceCategory(
                        key = "general_settings_category",
                        title = { Text(text = stringResource(id = R.string.general)) },
                    )
                    switchPreference(
                        key = "alert_sound_on_measurement_error_enabled",
                        title = { Text(text = stringResource(id = R.string.play_sound_on_measurement_error)) },
                        defaultValue = true,
                        enabled = {false},
                    )

                }
            }
        }

    }
}