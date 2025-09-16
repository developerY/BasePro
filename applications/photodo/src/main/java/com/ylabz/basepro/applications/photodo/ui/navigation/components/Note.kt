package com.ylabz.basepro.applications.photodo.ui.navigation.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


data class Note(
    val id: Long,
    val title: String,
    val content: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreateScreen(
    onBackClick: () -> Unit,
    onNoteCreated: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text("Create Note")
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        windowInsets = WindowInsets(0),
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    TopAppBar(
        title = {
            Text("Detail")
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        windowInsets = WindowInsets(0),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
        TopAppBar(
            title = {
                Text("Edit note")
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            windowInsets = WindowInsets(0),
        )
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    onNoteClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {


    TopAppBar(
        title = {
            Text("Notes")
        },
        actions = {
            IconButton(onClick = onCreateClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create note"
                )
            }
        },
        windowInsets = WindowInsets(0),
    )
}