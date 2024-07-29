package com.cadrikmdev.core.connectivity.data.di

import com.cadrikmdev.core.connectivity.data.BluetoothNodeDiscovery
import com.cadrikmdev.core.connectivity.data.messaging.BluetoothMessagingClient
import com.cadrikmdev.core.connectivity.domain.messaging.MessagingClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    singleOf(::BluetoothNodeDiscovery).bind<NodeDiscovery>()
    singleOf(::BluetoothMessagingClient).bind<MessagingClient>()
}