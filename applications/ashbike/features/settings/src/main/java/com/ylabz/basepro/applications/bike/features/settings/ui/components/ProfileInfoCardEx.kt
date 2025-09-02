package com.ylabz.basepro.applications.bike.features.settings.ui.components

//import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.graphics.Brush // No longer used
// Make sure this R class is correct for your 'settings' module
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.applications.bike.features.settings.R
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsEvent

@Composable
fun ProfileInfoCardEx(
    profile: ProfileData,
    isEditing: Boolean,
    isProfileIncomplete: Boolean,
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
                    MaterialTheme.colorScheme.surfaceContainerLow
                )
                .padding(16.dp)
        ) {
            if (!isEditing) {
                // — VIEW MODE
                Column {
                    if (isProfileIncomplete) {
                        Text(
                            text = stringResource(R.string.profile_incomplete_warning),
                            color = MaterialTheme.colorScheme.error, // Using theme error color
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile_person_icon_cd),
                            Modifier.size(48.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                profile.name, // Name is dynamic, not a static string resource
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                stringResource(
                                    R.string.profile_details_format,
                                    profile.heightCm,
                                    profile.weightKg
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onToggleEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.profile_edit_icon_cd)
                            )
                        }
                    }
                }
            } else {
                // — EDIT MODE
                var localName by rememberSaveable { mutableStateOf(profile.name) }
                var localHeight by rememberSaveable { mutableStateOf(profile.heightCm) }
                var localWeight by rememberSaveable { mutableStateOf(profile.weightKg) }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = localName,
                        onValueChange = { localName = it },
                        label = { Text(stringResource(R.string.profile_label_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = localHeight,
                            onValueChange = { localHeight = it },
                            label = { Text(stringResource(R.string.profile_label_height_cm)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = localWeight,
                            onValueChange = { localWeight = it },
                            label = { Text(stringResource(R.string.profile_label_weight_kg)) },
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
                            Text(stringResource(R.string.profile_button_save))
                        }
                    }
                }
            }
        }
    }
}

/*
@Preview(name = "View Mode Light - Incomplete", showBackground = true)
@Composable
fun ProfileInfoCardExPreviewViewModeIncomplete() {
    val profile = ProfileData(name = "John Doe", heightCm = "0", weightKg = "0")
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = false, isProfileIncomplete = true, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "View Mode Light - Complete", showBackground = true)
@Composable
fun ProfileInfoCardExPreviewViewModeComplete() {
    val profile = ProfileData(name = "John Doe", heightCm = "180", weightKg = "75")
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = false, isProfileIncomplete = false, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "Edit Mode Light", showBackground = true)
@Composable
fun ProfileInfoCardExPreviewEditMode() {
    val profile = ProfileData(name = "John Doe", heightCm = "180", weightKg = "75")
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = true, isProfileIncomplete = false, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "View Mode Dark - Incomplete", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileInfoCardExPreviewViewModeDarkIncomplete() {
    val profile = ProfileData(name = "John Doe", heightCm = "", weightKg = "") 
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = false, isProfileIncomplete = true, onToggleEdit = {}, onEvent = {})
    }
}

@Preview(name = "View Mode Dark - Complete", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileInfoCardExPreviewViewModeDarkComplete() {
    val profile = ProfileData(name = "John Doe", heightCm = "180", weightKg = "75")
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = false, isProfileIncomplete = false, onToggleEdit = {}, onEvent = {})
    }
}


@Preview(name = "Edit Mode Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileInfoCardExPreviewEditModeDark() {
    val profile = ProfileData(name = "John Doe", heightCm = "180", weightKg = "75")
    AshBikeTheme {
        ProfileInfoCardEx(profile = profile, isEditing = true, isProfileIncomplete = false, onToggleEdit = {}, onEvent = {})
    }
}
*/