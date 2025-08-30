package com.zoewave.basepro.applications.rxdigita.features.main.ui.components.home


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoewave.basepro.applications.rxdigita.features.main.ui.MedEvent
import com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens.MedicationDose
import com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens.MedicationSummaryScreen
import com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens.ScheduleGroup
import com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens.todaysSchedule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedDashboardContent(
    modifier: Modifier = Modifier,
    onMedEvent: (MedEvent) -> Unit, // viewModel::onEvent, // <<< MODIFIED LINE: Use the passed-in viewModel
    navTo: (String) -> Unit
) {
    MedicationSummaryScreen()
}
