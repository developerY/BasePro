package com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// DATA MODELS //

data class HealthStats(
    val bpSystolic: Int,
    val bpDiastolic: Int,
    val heartRate: Int,
    val hydrationPercent: Float,
    val lastUpdated: String
)

data class SymptomLog(val name: String, val severity: Int, val lastUpdated: String) // Severity 1-5
data class GlucoseLog(val value: Int, val unit: String, val status: String, val lastUpdated: String)

data class MedicationDose(val id: Int, val name: String, val dosage: String, val time: String, val isTaken: Boolean)
data class ScheduleGroup(val title: String, val doses: List<MedicationDose>)

// SAMPLE DATA //

val sampleHealthStats = HealthStats(122, 78, 67, 0.75f, "2:00 PM")
val sampleSymptom = SymptomLog("Headache", 2, "1:30 PM")
val sampleGlucose = GlucoseLog(105, "mg/dL", "Normal", "8:15 AM")

val sampleSchedule = listOf(
    ScheduleGroup("Morning", listOf(
        MedicationDose(1, "Lisinopril", "10 mg", "8:00 AM", true),
        MedicationDose(2, "Metformin", "500 mg", "9:00 AM", true),
        MedicationDose(3, "Vitamin D3", "1000 IU", "9:00 AM", false)
    )),
    ScheduleGroup("Afternoon", listOf(MedicationDose(4, "Ibuprofen", "200 mg", "1:00 PM", false))),
    ScheduleGroup("Evening", listOf(
        MedicationDose(5, "Atorvastatin", "20 mg", "8:00 PM", false),
        MedicationDose(6, "Amoxicillin", "250 mg", "10:00 PM", false)
    ))
)

// MAIN SCREEN COMPOSABLE //

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSummaryScreen() {
    var schedule by remember { mutableStateOf(sampleSchedule) }
    val onDoseTakenChange: (Int, Boolean) -> Unit = { doseId, taken ->
        schedule = schedule.map { group ->
            group.copy(doses = group.doses.map {
                if (it.id == doseId) it.copy(isTaken = taken) else it
            })
        }
    }

    Scaffold(
        topBar = { TopBar() },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HealthStatsCard(stats = sampleHealthStats) }
            item { SymptomTrackerCard(symptom = sampleSymptom) }
            item { BloodGlucoseCard(glucose = sampleGlucose) }
            item { ScheduleSectionCard(schedule = schedule, onDoseTakenChange = onDoseTakenChange) }
        }
    }
}

// UI COMPONENT COMPOSABLES //

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Today's Summary", fontWeight = FontWeight.Bold)
                Text(
                    text = sdf.format(Date()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun ExpandableCard(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand or collapse"
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    content()
                }
            }
        }
    }
}


@Composable
fun HealthStatsCard(stats: HealthStats) {
    var isExpanded by remember { mutableStateOf(true) }
    ExpandableCard("Current Health", Icons.Outlined.MonitorHeart, isExpanded, { isExpanded = !isExpanded }) {
        Text("Last updated: ${stats.lastUpdated}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            HealthStatItem(icon = Icons.Outlined.MonitorHeart, label = "Blood Pressure", value = "${stats.bpSystolic}/${stats.bpDiastolic}", unit = "mmHg")
            HealthStatItem(icon = Icons.Outlined.FavoriteBorder, label = "Heart Rate", value = "${stats.heartRate}", unit = "bpm")
            HealthStatItem(icon = Icons.Outlined.WaterDrop, label = "Hydration", value = "${(stats.hydrationPercent * 100).toInt()}%", unit = "Goal")
        }
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { stats.hydrationPercent },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small)
        )
    }
}

@Composable
fun SymptomTrackerCard(symptom: SymptomLog) {
    var isExpanded by remember { mutableStateOf(false) }
    ExpandableCard("Symptom Tracker", Icons.Outlined.Mood, isExpanded, { isExpanded = !isExpanded }) {
        Text("Last log: ${symptom.lastUpdated}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(symptom.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text("Severity: ${symptom.severity}/5", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun BloodGlucoseCard(glucose: GlucoseLog) {
    var isExpanded by remember { mutableStateOf(false) }
    ExpandableCard("Blood Glucose", Icons.Outlined.Bloodtype, isExpanded, { isExpanded = !isExpanded }) {
        Text("Last reading: ${glucose.lastUpdated}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("${glucose.value} ${glucose.unit}", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            Text(
                glucose.status,
                style = MaterialTheme.typography.titleMedium,
                color = when (glucose.status) {
                    "Normal" -> Color(0xFF388E3C)
                    "High" -> Color(0xFFD32F2F)
                    else -> Color(0xFFF57C00)
                }
            )
        }
    }
}


@Composable
fun ScheduleSectionCard(
    schedule: List<ScheduleGroup>,
    onDoseTakenChange: (Int, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val totalDoses = schedule.sumOf { it.doses.size }
    val takenDoses = schedule.sumOf { group -> group.doses.count { it.isTaken } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Today's Schedule", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Text("$takenDoses of $totalDoses Taken", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand or collapse schedule",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    schedule.forEach { group ->
                        ExpandableScheduleGroup(group = group, onDoseTakenChange = onDoseTakenChange)
                    }
                }
            }
        }
    }
}

@Composable
fun HealthStatItem(icon: ImageVector, label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun ExpandableScheduleGroup(group: ScheduleGroup, onDoseTakenChange: (Int, Boolean) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val takenCount = group.doses.count { it.isTaken }
    val totalCount = group.doses.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(group.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text("$takenCount of $totalCount Taken", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand or collapse",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    group.doses.forEach { dose ->
                        MedicationDoseItem(dose = dose, onTakenChange = { onDoseTakenChange(dose.id, it) })
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationDoseItem(dose: MedicationDose, onTakenChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = dose.isTaken, onCheckedChange = onTakenChange)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = dose.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${dose.dosage} at ${dose.time}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// PREVIEW FUNCTION //

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun MedicationSummaryScreenPreview() {
    MaterialTheme {
        Surface {
            MedicationSummaryScreen()
        }
    }
}
