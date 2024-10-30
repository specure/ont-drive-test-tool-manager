package com.specure.updater.domain

import kotlinx.coroutines.flow.SharedFlow

interface Updater {

    val updateStatus: SharedFlow<UpdatingStatus>

    suspend fun checkForSelfUpdate(repoUrl: String)

    suspend fun downloadAndInstallSelfUpdate()

    suspend fun checkAndInstall(repoUrl: String)



    val latestReleasedVersionStatus: SharedFlow<UpdatingStatus>

    suspend fun getLatestReleasedVersion(repoUrl: String)

    fun getLatestVersion(version1: String?, version2: String?): String?
}
