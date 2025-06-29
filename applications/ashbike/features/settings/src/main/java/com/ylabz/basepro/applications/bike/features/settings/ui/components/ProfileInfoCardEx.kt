package com.ylabz.basepro.applications.bike.features.settings.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsEvent
import com.ylabz.basepro.core.ui.theme.AshBikeTheme

@Composable
fun ProfileInfoCardEx(
    profile: ProfileData,
    isEditing: Boolean,
    onToggleEdit: () -> Unit,
    onEvent: (SettingsEvent) -> Unit
) {
    Card(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLow // Replaced hardcoded gradient
                )
                .padding(16.dp)
        ) {
            if (!isEditing) {
                // — VIEW MODE: always show latest profile from uiState
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, Modifier.size(48.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            profile.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "Height: ${profile.heightCm} cm | Weight: ${profile.weightKg} kg",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onToggleEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }

            } else {
                // — EDIT MODE: local, saveable state initialized once
                var localName by rememberSaveable { mutableStateOf(profile.name) }
                var localHeight by rememberSaveable { mutableStateOf(profile.heightCm) }
                var localWeight by rememberSaveable { mutableStateOf(profile.weightKg) }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = localName,
                        onValueChange = { localName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = localHeight,
                            onValueChange = { localHeight = it },
                            label = { Text("Height (cm)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = localWeight,
                            onValueChange = { localWeight = it },
                            label = { Text("Weight (kg)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            // Single event to save all three fields at once
                            onEvent(
                                SettingsEvent.SaveProfile(
                                    ProfileData(
                                        name = localName,
                                        heightCm = localHeight,
                                        weightKg = localWeight
                                    )
                                )
                            )
                            onToggleEdit()
                        }) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "View Mode Light", showBackground = true)
@Composable
fun ProfileInfoCardExPreviewViewMode() {
    val profile = ProfileData(
        name = "John Doe",
        heightCm = "180",
        weightKg = "75"
    )
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = false, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "Edit Mode Light", showBackground = true)
@Composable
fun ProfileInfoCardExPreviewEditMode() {
    val profile = ProfileData(
        name = "John Doe",
        heightCm = "180",
        weightKg = "75"
    )
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = true, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "View Mode Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileInfoCardExPreviewViewModeDark() {
    val profile = ProfileData(
        name = "John Doe",
        heightCm = "180",
        weightKg = "75"
    )
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = false, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "Edit Mode Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileInfoCardExPreviewEditModeDark() {
    val profile = ProfileData(
        name = "John Doe",
        heightCm = "180",
        weightKg = "75"
    )
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = true, onToggleEdit = {}, onEvent = {})
    }
}