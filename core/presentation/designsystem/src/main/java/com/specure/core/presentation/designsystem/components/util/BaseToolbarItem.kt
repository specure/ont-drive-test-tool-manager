package com.specure.core.presentation.designsystem.components.util

class BaseToolbarItem(
    val item: DropDownItem,
    val action: () -> Unit =  {}
)