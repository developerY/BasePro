package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity

@Composable
fun HomeScreen(
    // It now accepts a list of ProjectEntity
    projects: List<ProjectEntity>,
    // The click listener provides the projectId (Long)
    onCategoryClick: (Long) -> Unit,
    onAddNewCategoryClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(projects) { project ->
            Card(
                modifier = Modifier
                    .clickable { onCategoryClick(project.projectId) }
            ) {
                Text(
                    text = project.name,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}