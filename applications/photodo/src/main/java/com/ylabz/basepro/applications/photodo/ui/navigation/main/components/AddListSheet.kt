package com.ylabz.basepro.applications.photodo.ui.navigation.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenEvent
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListSheet(
    state: MainScreenUiState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val onDismiss = { onEvent(MainScreenEvent.OnBottomSheetDismissed) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var listName by remember { mutableStateOf("") }
    var listDescription by remember { mutableStateOf("") }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    // Initialize selection with the last selected ID from state
    var selectedCategory by remember(state.categories, state.lastSelectedCategoryId) {
        mutableStateOf(
            state.categories.find { it.categoryId == state.lastSelectedCategoryId }
                ?: state.categories.firstOrNull()
        )
    }

    val isSaveEnabled = listName.isNotBlank() && selectedCategory != null

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    fun handleSave() {
        if (isSaveEnabled && selectedCategory != null) {
            scope.launch {
                sheetState.hide()
                // Dispatch the event directly
                onEvent(MainScreenEvent.OnSaveList(
                    title = listName,
                    description = listDescription,
                    categoryId = selectedCategory!!.categoryId
                ))
                // Dismiss is handled by the ViewModel consuming the event,
                // but we can force a dismiss event to be safe or if VM doesn't auto-dismiss.
                // Based on your VM logic, you usually send OnBottomSheetDismissed after.
                onEvent(MainScreenEvent.OnBottomSheetDismissed)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
        ) {
            Text("Add New List", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 24.dp))

            // Category Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Select Category") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { isCategoryExpanded = true })

                DropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false }
                ) {
                    state.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = listName,
                onValueChange = { listName = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                label = { Text("List Name") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, "List Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = listDescription,
                onValueChange = { listDescription = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, "List Description") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { handleSave() })
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { handleSave() }, enabled = isSaveEnabled) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.height(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save List")
                }
            }
        }
    }
}