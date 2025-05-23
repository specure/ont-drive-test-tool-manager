package com.specure.manager.presentation.about

sealed interface AboutScreenAction {
    data object OnCheckUpdateClick : AboutScreenAction
    data object OnInstallUpdateClick : AboutScreenAction
}