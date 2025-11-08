package com.ylabz.basepro.applications.photodo.features.home.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity

@Composable
fun CategoryList(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    onCategoryClick: (CategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("CategoryList", "Recomposing with ${categories.size} categories")
    LazyColumn(
        modifier = modifier.padding(8.dp).background(Color.Yellow),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Source: CategoryList.kt")
        }
        items(categories) { category ->
            ListItem(
                headlineContent = { 
                    Column {
                        Text(text = category.name)
                        Text("CategoryList.kt", style = MaterialTheme.typography.bodySmall)
                    }
                 },
                modifier = Modifier.clickable { onCategoryClick(category) },
                colors = if (category.categoryId == selectedCategory?.categoryId) {
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                } else {
                    ListItemDefaults.colors()
                }
            )
        }
    }
}
