package com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// DATA MODELS //

data class MedicationDose(
    val id: Int,
    val name: String,
    val dosage: String,
    val time: String,
    val isTaken: Boolean,
    val isPrn: Boolean = false, // Pro re nata (as-needed)
    val knownSideEffects: List<String> = emptyList() // Populated from label scan
)

data class SymptomLogEntry(val name: String, val severity: Int)

// Other data models remain the same...
data class HealthStats(val bpSystolic: Int, val bpDiastolic: Int, val heartRate: Int, val hydrationPercent: Float, val lastUpdated: String)
data class SymptomLog(val name: String, val severity: Int, val lastUpdated: String)
data class GlucoseLog(val value: Int, val unit: String, val status: String, val lastUpdated: String)
data class ScheduleGroup(val title: String, val doses: List<MedicationDose>)

// SAMPLE DATA //

val sampleHealthStats = HealthStats(122, 78, 67, 0.75f, "2:00 PM")
val sampleSymptom = SymptomLog("Headache", 2, "1:30 PM")
val sampleGlucose = GlucoseLog(105, "mg/dL", "Normal", "8:15 AM")

val sampleSchedule = listOf(
    ScheduleGroup("Morning", listOf(
        MedicationDose(1, "Lisinopril", "10 mg", "8:00 AM", true, knownSideEffects = listOf("Dizziness", "Headache")),
        MedicationDose(2, "Metformin", "500 mg", "9:00 AM", true, knownSideEffects = listOf("Nausea", "Diarrhea"))
    )),
    ScheduleGroup("As Needed", listOf(
        MedicationDose(4, "Ibuprofen", "200 mg", "PRN", false, isPrn = true, knownSideEffects = listOf("Stomach Pain"))
    )),
    ScheduleGroup("Evening", listOf(
        MedicationDose(5, "Atorvastatin", "20 mg", "8:00 PM", false, knownSideEffects = listOf("Muscle Pain"))
    ))
)

// MAIN SCREEN COMPOSABLE //

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSummaryScreen() {
    var schedule by remember { mutableStateOf(sampleSchedule) }
    var doseToLog by remember { mutableStateOf<MedicationDose?>(null) }
    var showLogSymptomDialog by remember { mutableStateOf(false) }
    var symptomToLink by remember { mutableStateOf<SymptomLogEntry?>(null) }

    val onDoseTakenChange: (Int, Boolean) -> Unit = { doseId, taken ->
        schedule = schedule.map { group ->
            group.copy(doses = group.doses.map {
                if (it.id == doseId) it.copy(isTaken = taken) else it
            })
        }
    }

    val onConfirmDoseLog: (MedicationDose) -> Unit = { dose ->
        onDoseTakenChange(dose.id, true)
        doseToLog = null
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
            item {
                SymptomTrackerCard(
                    symptom = sampleSymptom,
                    onLogSymptomClick = { showLogSymptomDialog = true }
                )
            }
            item { BloodGlucoseCard(glucose = sampleGlucose) }

            item {
                ScheduleSectionCard(
                    schedule = schedule,
                    onDoseTakenChange = onDoseTakenChange,
                    onTakePrnDose = { dose -> doseToLog = dose }
                )
            }
        }

        if (doseToLog != null) {
            TakeDoseDialog(
                dose = doseToLog!!,
                onDismiss = { doseToLog = null },
                onConfirm = { onConfirmDoseLog(it) }
            )
        }

        if (showLogSymptomDialog) {
            LogSymptomDialog(
                onDismiss = { showLogSymptomDialog = false },
                onConfirm = { name, severity ->
                    showLogSymptomDialog = false
                    symptomToLink = SymptomLogEntry(name, severity)
                }
            )
        }

        if (symptomToLink != null) {
            LinkMedicationDialog(
                symptom = symptomToLink!!,
                recentMeds = schedule.flatMap { it.doses }.filter { it.isTaken },
                onDismiss = { symptomToLink = null },
                onConfirm = {
                    // In a real app, save the link
                    symptomToLink = null
                }
            )
        }
    }
}

// DIALOG COMPOSABLES //
@Composable
fun LinkMedicationDialog(
    symptom: SymptomLogEntry,
    recentMeds: List<MedicationDose>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Link to Recent Medications?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Did '${symptom.name}' occur after taking any of these?")
                Spacer(modifier = Modifier.height(16.dp))

                recentMeds.forEach { med ->
                    LinkableMedicationItem(med = med, symptomName = symptom.name)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
                    Text("Save Links")
                }
            }
        }
    }
}

@Composable
fun LinkableMedicationItem(med: MedicationDose, symptomName: String) {
    var isChecked by remember { mutableStateOf(false) }
    // Simple case-insensitive check
    val isCommonSideEffect = med.knownSideEffects.any { it.equals(symptomName, ignoreCase = true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isChecked = !isChecked },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(med.name, style = MaterialTheme.typography.bodyLarge)
            if (isCommonSideEffect) {
                Text(
                    "Common side effect",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSymptomDialog(
    onDismiss: () -> Unit,
    onConfirm: (symptomName: String, severity: Int) -> Unit
) {
    var symptomName by remember { mutableStateOf("") }
    var severity by remember { mutableFloatStateOf(1f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Log Symptom", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = symptomName,
                    onValueChange = { symptomName = it },
                    label = { Text("Symptom Name (e.g. Headache)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Severity: ${severity.roundToInt()}", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = severity,
                    onValueChange = { severity = it },
                    valueRange = 1f..5f,
                    steps = 3
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onConfirm(symptomName, severity.roundToInt()) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = symptomName.isNotBlank()
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun TakeDoseDialog(
    dose: MedicationDose,
    onDismiss: () -> Unit,
    onConfirm: (MedicationDose) -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Log Dose", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(dose.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("What is this dose for?", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        Toast.makeText(context, "Linking to symptom...", Toast.LENGTH_SHORT).show()
                        onConfirm(dose)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("For a Symptom (e.g. Headache)")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Logging as 'Other'", Toast.LENGTH_SHORT).show()
                        onConfirm(dose)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Other Reason")
                }
            }
        }
    }
}


// MODIFIED COMPOSABLES //

@Composable
fun ExpandableCard(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onClick: () -> Unit,
    action: (@Composable () -> Unit)? = null, // New parameter for an action icon
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                Text(text = title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f).padding(start = 12.dp))
                if (action != null) {
                    action()
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand or collapse",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) { content() }
            }
        }
    }
}

@Composable
fun SymptomTrackerCard(
    symptom: SymptomLog,
    onLogSymptomClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    ExpandableCard(
        title = "Symptom Tracker",
        icon = Icons.Outlined.Mood,
        isExpanded = isExpanded,
        onClick = { isExpanded = !isExpanded },
        action = {
            IconButton(onClick = onLogSymptomClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Log a new symptom")
            }
        }
    ) {
        Text("Last log: ${symptom.lastUpdated}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(symptom.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text("Severity: ${symptom.severity}/5", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ScheduleSectionCard(
    schedule: List<ScheduleGroup>,
    onDoseTakenChange: (Int, Boolean) -> Unit,
    onTakePrnDose: (MedicationDose) -> Unit
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
                        ExpandableScheduleGroup(
                            group = group,
                            onDoseTakenChange = onDoseTakenChange,
                            onTakePrnDose = onTakePrnDose
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableScheduleGroup(
    group: ScheduleGroup,
    onDoseTakenChange: (Int, Boolean) -> Unit,
    onTakePrnDose: (MedicationDose) -> Unit
) {
    var isExpanded by remember { mutableStateOf(group.title == "As Needed") }
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
                if (totalCount > 0) {
                    Text("$takenCount of $totalCount Taken", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
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
                        MedicationDoseItem(
                            dose = dose,
                            onCheckedChange = { onDoseTakenChange(dose.id, it) },
                            onTakeClick = { onTakePrnDose(dose) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationDoseItem(
    dose: MedicationDose,
    onCheckedChange: (Boolean) -> Unit,
    onTakeClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = dose.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${dose.dosage} at ${dose.time}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(16.dp))

        if (dose.isPrn) {
            Button(onClick = onTakeClick, enabled = !dose.isTaken) {
                Text(if (dose.isTaken) "Taken" else "Take")
            }
        } else {
            Checkbox(checked = dose.isTaken, onCheckedChange = onCheckedChange)
        }
    }
}


// UNCHANGED COMPOSABLES //
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
fun HealthStatsCard(stats: HealthStats) {
    var isExpanded by remember { mutableStateOf(true) }
    ExpandableCard("Current Health", Icons.Outlined.MonitorHeart, isExpanded, { isExpanded = !isExpanded }) {
        Text("Last updated: ${stats.lastUpdated}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Top) {
            HealthStatItem(icon = Icons.Outlined.MonitorHeart, label = "Blood Pressure", value = "${stats.bpSystolic}/${stats.bpDiastolic}", unit = "mmHg")
            HealthStatItem(icon = Icons.Outlined.FavoriteBorder, label = "Heart Rate", value = "${stats.heartRate}", unit = "bpm")
            HealthStatItem(icon = Icons.Outlined.WaterDrop, label = "Hydration", value = "${(stats.hydrationPercent * 100).toInt()}%", unit = "Goal")
        }
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(progress = { stats.hydrationPercent }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small))
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
            Text(glucose.status, style = MaterialTheme.typography.titleMedium, color = when (glucose.status) { "Normal" -> Color(0xFF388E3C) "High" -> Color(0xFFD32F2F) else -> Color(0xFFF57C00) })
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
