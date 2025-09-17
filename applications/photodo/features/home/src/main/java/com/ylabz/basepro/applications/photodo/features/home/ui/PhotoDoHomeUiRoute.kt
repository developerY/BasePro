package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PhotoDoHomeUiRoute(navTo: (String) -> Unit) {
    HomeScreen(
        categories = listOf("Home", "Car", "School", "Shopping"),
        onCategoryClick = navTo,
        onAddNewCategoryClick = { /* Handle add new category click */ }
    )
}