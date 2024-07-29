package com.cadrikmdev.core.connectivity.data.messaging

import kotlinx.serialization.Serializable

@Serializable
sealed interface MessagingActionDto {
    @Serializable
    data object Start : MessagingActionDto

    @Serializable
    data object Stop : MessagingActionDto

    @Serializable
    data class StatusUpdate(val status: String) : MessagingActionDto
}