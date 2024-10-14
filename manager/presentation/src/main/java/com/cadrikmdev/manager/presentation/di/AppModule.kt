package com.cadrikmdev.manager.presentation.di


import com.cadrikmdev.manager.presentation.about.AboutScreenViewModel
import com.cadrikmdev.manager.presentation.manager_overview.ManagerOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val managerPresentationModule = module {
    viewModelOf(::ManagerOverviewViewModel)
    viewModelOf(::AboutScreenViewModel)
}