package com.zoewave.basepro.applications.rxdigita.features.main.ui.components.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



/**
 * Data class representing a single medication dose for the schedule.
 *
 * @param name The name of the medication.
 * @param dosage The prescribed dosage (e.g., "500 mg").
 * @param time The specific time for this dose (e.g., "8:00 AM").
 * @param taken Whether the dose has been marked as taken.
 */
data class MedicationDoseList(
    val id: Int,
    val name: String,
    val dosage: String,
    val time: String,
    var taken: Boolean
)

/**
 * Groups medication doses by time of day.
 */
data class ScheduleGroupList(
    val title: String,
    val doses: List<MedicationDoseList>
)

// Sample data for the summary screen
val todaysSchedule = listOf(
    ScheduleGroupList(
        title = "Morning",
        doses = listOf(
            MedicationDoseList(1, "Lisinopril", "10 mg", "8:00 AM", true),
            MedicationDoseList(2, "Metformin", "500 mg", "9:00 AM", false),
            MedicationDoseList(3, "Vitamin D3", "1000 IU", "9:00 AM", false)
        )
    ),
    ScheduleGroupList(
        title = "Evening",
        doses = listOf(
            MedicationDoseList(4, "Atorvastatin", "20 mg", "8:00 PM", false),
            MedicationDoseList(5, "Amoxicillin", "250 mg", "10:00 PM", false)
        )
    )
)



/**
 * The main composable for the Medication Summary Screen (Dashboard).
 * It uses a Scaffold to provide a standard Material 3 layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSumListScreen() {
    // State to hold the schedule, allowing for updates (e.g., checking off a dose)
    var schedule by remember { mutableStateOf(todaysSchedule) }

    val onDoseTakenChange: (MedicationDoseList, Boolean) -> Unit = { dose, taken ->
        val newSchedule = schedule.map { group ->
            group.copy(doses = group.doses.map {
                if (it.id == dose.id) {
                    it.copy(taken = taken)
                } else {
                    it
                }
            })
        }
        schedule = newSchedule
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Medication Summary", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CurrentTimeCard() }
            item { SummaryStatsCard(schedule = schedule) }
            item { ActionButtonsRow() }

            // "Today's Schedule" sections
            schedule.forEach { group ->
                item {
                    Text(
                        text = group.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(group.doses) { dose ->
                    MedicationDoseListItem(
                        dose = dose,
                        onTakenChange = { taken -> onDoseTakenChange(dose, taken) }
                    )
                }
            }
        }
    }
}

/**
 * Displays the current date and time.
 */
@Composable
fun CurrentTimeCard() {
    val sdfDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val sdfTime = SimpleDateFormat("h:mm a", Locale.getDefault())
    val currentDate = sdfDate.format(Date())
    val currentTime = sdfTime.format(Date())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentTime,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Displays summary statistics like adherence percentage.
 */
@Composable
fun SummaryStatsCard(schedule: List<ScheduleGroupList>) {
    val totalDoses = schedule.sumOf { it.doses.size }
    val takenDoses = schedule.sumOf { group -> group.doses.count { it.taken } }
    val adherence = if (totalDoses > 0) (takenDoses.toFloat() / totalDoses * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(icon = Icons.Default.CheckCircle, label = "Today's Adherence", value = "$adherence%")
            StatItem(icon = Icons.Default.Medication, label = "Meds Taken", value = "$takenDoses / $totalDoses")
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * A row of primary action buttons.
 */
@Composable
fun ActionButtonsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { /* Navigate to full schedule */ }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Full Schedule")
        }
        Button(onClick = { /* Handle Add Medication */ }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add New Med")
        }
    }
}


/**
 * Displays a single medication dose with a checkbox.
 */
@Composable
fun MedicationDoseListItem(
    dose: MedicationDoseList,
    onTakenChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (dose.taken) MaterialTheme.colorScheme.tertiaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = dose.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "${dose.dosage} at ${dose.time}", style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(
                checked = dose.taken,
                onCheckedChange = onTakenChange,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Preview function for the MedicationSummaryScreen.
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun MedicationSumListScreenPreview() {
    MaterialTheme {
        Surface {
            MedicationSummaryScreen()
        }
    }
}
