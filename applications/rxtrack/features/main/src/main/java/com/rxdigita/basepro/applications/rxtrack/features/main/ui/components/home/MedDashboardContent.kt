package com.rxdigita.basepro.applications.rxtrack.features.main.ui.components.home


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rxdigita.basepro.applications.rxtrack.features.main.ui.MedEvent
import com.rxdigita.basepro.applications.rxtrack.features.main.ui.components.screens.MedicationSummaryScreen

@Composable
fun MedDashboardContent(
    modifier: Modifier = Modifier,
    onMedEvent: (MedEvent) -> Unit, // viewModel::onEvent, // <<< MODIFIED LINE: Use the passed-in viewModel
    navTo: (String) -> Unit
) {
    MedicationSummaryScreen()
}
