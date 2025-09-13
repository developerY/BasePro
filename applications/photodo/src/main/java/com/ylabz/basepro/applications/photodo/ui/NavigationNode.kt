package com.ylabz.basepro.applications.photodo.ui

import androidx.navigation3.runtime.NavKey

interface NavigationNode : NavKey {
    val route: String
        get() = this::class.java.name
}