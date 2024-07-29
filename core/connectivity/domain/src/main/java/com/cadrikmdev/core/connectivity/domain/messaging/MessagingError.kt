package com.cadrikmdev.core.connectivity.domain.messaging

import com.cadrikmdev.domain.util.Error

enum class MessagingError : Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}