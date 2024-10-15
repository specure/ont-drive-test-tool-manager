package com.cadrikmdev.manager.presentation.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cadrikmdev.core.domain.config.Config
import com.cadrikmdev.core.presentation.designsystem.SignalTrackerManagerTheme
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerScaffold
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerToolbar
import com.cadrikmdev.core.presentation.ui.config.AppConfig
import com.cadrikmdev.manager.presentation.R
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreenRoot(
    onBackClick: () -> Unit,
    viewModel: SettingsScreenViewModel = koinViewModel()
) {
    SettingsScreen(
        onBackClick,
        onAction = { },
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAction: (SettingsAction) -> Unit,
    viewModel: SettingsScreenViewModel
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
                        key = Config.ALERT_SOUND_ON_TEST_ERROR_ENABLED_CONFIG_KEY,
                        title = { Text(text = stringResource(id = R.string.play_sound_on_measurement_error)) },
                        defaultValue = viewModel.appConfig.getIsAlertSoundOnTestErrorEnabledDefault(),
                    )

                }
            }
        }

    }
}