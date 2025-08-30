package com.zoewave.basepro.applications.rxdigita.features.medlist.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent

/**
 * Data class representing a single medication.
 *
 * @param name The name of the medication.
 * @param dosage The prescribed dosage (e.g., "500 mg").
 * @param schedule When the medication should be taken (e.g., "Twice a day").
 * @param time The specific time for the next dose (e.g., "Next: 8:00 PM").
 * @param icon An icon representing the medication type (e.g., pill, liquid).
 */
data class Medication(
    val name: String,
    val dosage: String,
    val schedule: String,
    val time: String,
    val icon: ImageVector
)

/**
 * A sample list of medications for preview and development purposes.
 */
val sampleMedications = listOf(
    Medication("Lisinopril", "10 mg", "Once a day", "Next: 8:00 AM", Icons.Default.Medication),
    Medication("Metformin", "500 mg", "Twice a day", "Next: 9:00 AM", Icons.Default.Vaccines),
    Medication("Atorvastatin", "20 mg", "Once a day", "Next: 8:00 PM", Icons.Default.Medication),
    Medication("Amoxicillin", "250 mg", "Every 8 hours", "Next: 10:00 PM", Icons.Default.Vaccines),
    Medication("Vitamin D3", "1000 IU", "Once a day", "Next: 9:00 AM", Icons.Default.Medication)
)


@Composable
fun MedListContent(
    modifier: Modifier = Modifier,
    onMedListEvent: (MedListEvent) -> Unit,
    navTo: (String) -> Unit
) {
    MedicationSummaryScreen(sampleMedications)
}


/**
 * The main composable for the Medication Summary Screen.
 * It uses a Scaffold to provide a standard Material 3 layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSummaryScreen(medications: List<Medication>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Medications", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handle FAB click */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // LazyColumn is used for efficiently displaying a scrollable list of items.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(medications) { medication ->
                MedicationInfoCard(medication = medication)
            }
        }
    }
}

/**
 * A card composable that displays the information for a single medication.
 *
 * @param medication The medication data to display.
 */
@Composable
fun MedicationInfoCard(medication: Medication) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        onClick = { /* Handle card click for details */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon representing the medication type
            Icon(
                imageVector = medication.icon,
                contentDescription = "Medication Icon",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Column for medication details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${medication.dosage} ・ ${medication.schedule}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = medication.time,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Arrow icon to indicate clickability
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "View Details",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Preview function for the MedicationSummaryScreen.
 * This allows you to see the UI in Android Studio's preview panel.
 */
/*
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun MedicationSummaryScreenPreview() {
    // It's a good practice to wrap previews in your app's theme.
    // Using a default MaterialTheme here for demonstration.
    MaterialTheme {
        Surface {
            MedicationSummaryScreen(medications = sampleMedications)
        }
    }
}

 */