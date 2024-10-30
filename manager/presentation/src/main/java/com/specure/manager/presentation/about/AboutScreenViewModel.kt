package com.specure.manager.presentation.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specure.core.domain.package_info.PackageInfoProvider
import com.specure.manager.presentation.BuildConfig
import com.specure.updater.domain.Updater
import com.specure.updater.domain.UpdatingStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AboutScreenViewModel(
    packageInfo: PackageInfoProvider,
    private val updater: Updater
) : ViewModel() {
    val versionName = packageInfo.versionName

    var updateState = updater.updateStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UpdatingStatus.Idle
    )

    fun onAction(action: AboutScreenAction) {
        when (action) {
            AboutScreenAction.OnCheckUpdateClick -> {
                viewModelScope.launch {
                    updater.checkForSelfUpdate(BuildConfig.GITHUB_API_MANAGER_REPO_URL)
                }
            }

            AboutScreenAction.OnInstallUpdateClick -> {
                viewModelScope.launch {
                    updater.downloadAndInstallSelfUpdate()
                }
            }
        }
    }

}