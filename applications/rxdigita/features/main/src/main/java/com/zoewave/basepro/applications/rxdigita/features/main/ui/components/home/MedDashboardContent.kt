package com.zoewave.basepro.applications.rxdigita.features.main.ui.components.home


//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zoewave.basepro.applications.rxdigita.features.main.ui.MedEvent
import com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens.MedicationSummaryScreen

@Composable
fun MedDashboardContent(
    modifier: Modifier = Modifier,
    onMedEvent: (MedEvent) -> Unit, // viewModel::onEvent, // <<< MODIFIED LINE: Use the passed-in viewModel
    navTo: (String) -> Unit
) {
    MedicationSummaryScreen()
}
