package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity

@Composable
fun CategoryList(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    onCategoryClick: (CategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            ListItem(
                headlineContent = { Text(text = category.name) },
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
