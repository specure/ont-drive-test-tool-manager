package com.cadrikmdev.core.connectivity.domain.messaging

sealed interface MessagingAction {
    data object Start : MessagingAction
    data object Stop : MessagingAction
    data class StatusUpdate(val status: String) : MessagingAction
}