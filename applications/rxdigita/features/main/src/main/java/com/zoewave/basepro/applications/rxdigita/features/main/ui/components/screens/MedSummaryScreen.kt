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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorHeart
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
    val hydrationPercent: Float, // 0.0f to 1.0f
    val lastUpdated: String
)

data class MedicationDose(
    val id: Int,
    val name: String,
    val dosage: String,
    val time: String,
    val isTaken: Boolean
)

data class ScheduleGroup(
    val title: String,
    val doses: List<MedicationDose>
)

// SAMPLE DATA //

val sampleHealthStats = HealthStats(
    bpSystolic = 122,
    bpDiastolic = 78,
    heartRate = 67,
    hydrationPercent = 0.75f,
    lastUpdated = "2:00 PM"
)

val sampleSchedule = listOf(
    ScheduleGroup(
        title = "Morning",
        doses = listOf(
            MedicationDose(1, "Lisinopril", "10 mg", "8:00 AM", true),
            MedicationDose(2, "Metformin", "500 mg", "9:00 AM", true),
            MedicationDose(3, "Vitamin D3", "1000 IU", "9:00 AM", false)
        )
    ),
    ScheduleGroup(
        title = "Afternoon",
        doses = listOf(
            MedicationDose(4, "Ibuprofen", "200 mg", "1:00 PM", false)
        )
    ),
    ScheduleGroup(
        title = "Evening",
        doses = listOf(
            MedicationDose(5, "Atorvastatin", "20 mg", "8:00 PM", false),
            MedicationDose(6, "Amoxicillin", "250 mg", "10:00 PM", false)
        )
    )
)

// MAIN SCREEN COMPOSABLE //

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSummaryScreen() {
    var schedule by remember { mutableStateOf(sampleSchedule) }
    var isScheduleVisible by remember { mutableStateOf(true) }

    val onDoseTakenChange: (Int, Boolean) -> Unit = { doseId, taken ->
        schedule = schedule.map { group ->
            group.copy(doses = group.doses.map {
                if (it.id == doseId) it.copy(isTaken = taken) else it
            })
        }
    }

    Scaffold(
        topBar = {
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HealthStatsCard(stats = sampleHealthStats)
            }
            item {
                ScheduleSectionHeader(
                    isExpanded = isScheduleVisible,
                    schedule = schedule,
                    onClick = { isScheduleVisible = !isScheduleVisible }
                )
            }
            // The actual list of medication cards is now animated
            if (isScheduleVisible) {
                items(schedule) { group ->
                    ExpandableScheduleCard(
                        group = group,
                        onDoseTakenChange = onDoseTakenChange
                    )
                }
            }
        }
    }
}

// UI COMPONENT COMPOSABLES //

@Composable
fun ScheduleSectionHeader(
    isExpanded: Boolean,
    schedule: List<ScheduleGroup>,
    onClick: () -> Unit
) {
    val totalDoses = schedule.sumOf { it.doses.size }
    val takenDoses = schedule.sumOf { group -> group.doses.count { it.isTaken } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Today's Schedule",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        if (!isExpanded) {
            Text(
                "$takenDoses of $totalDoses Taken",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Expand or collapse schedule"
        )
    }
}


@Composable
fun HealthStatsCard(stats: HealthStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small),
            )
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
fun ExpandableScheduleCard(
    group: ScheduleGroup,
    onDoseTakenChange: (Int, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val takenCount = group.doses.count { it.isTaken }
    val totalCount = group.doses.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(group.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Text(
                    "$takenCount of $totalCount Taken",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
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
            Text(
                text = dose.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${dose.dosage} at ${dose.time}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
