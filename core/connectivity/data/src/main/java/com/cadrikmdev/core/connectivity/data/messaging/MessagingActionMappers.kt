package com.cadrikmdev.core.connectivity.data.messaging

import com.cadrikmdev.core.connectivity.domain.messaging.MessagingAction

fun MessagingAction.toMessagingActionDto(): MessagingActionDto {
    return when (this) {
        is MessagingAction.StatusUpdate -> MessagingActionDto.StatusUpdate(status)
        MessagingAction.Start -> MessagingActionDto.Start
        is MessagingAction.Stop -> MessagingActionDto.Stop
    }
}

fun MessagingActionDto.toMessagingAction(): MessagingAction {
    return when (this) {
        MessagingActionDto.Start -> MessagingAction.Start
        is MessagingActionDto.Stop -> MessagingAction.Stop
        is MessagingActionDto.StatusUpdate -> MessagingAction.StatusUpdate(status)
    }
}