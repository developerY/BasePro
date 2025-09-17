package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * The main home screen composable for the PhoToDo app.
 *
 * It displays either a list of existing categories or an empty state
 * with suggestions to create new ones.
 *
 * @param categories The list of category names to display.
 * @param onCategoryClick Lambda function to be invoked when a category is clicked.
 * @param onAddNewCategoryClick Lambda function for the 'Add New Category' button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    onAddNewCategoryClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PhoToDo 📸") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        // Decide which view to show based on whether the category list is empty
        if (categories.isEmpty()) {
            EmptyStateView(
                modifier = Modifier.padding(innerPadding),
                onAddNewCategoryClick = onAddNewCategoryClick
            )
        } else {
            CategoryListView(
                modifier = Modifier.padding(innerPadding),
                categories = categories,
                onCategoryClick = onCategoryClick
            )
        }
    }
}

/**
 * Displays the list of photo categories.
 */
@Composable
private fun CategoryListView(
    modifier: Modifier = Modifier,
    categories: List<String>,
    onCategoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                categoryName = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

/**
 * A single card representing a category in the list.
 */
@Composable
private fun CategoryItem(categoryName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = categoryName, style = MaterialTheme.typography.titleLarge)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null, // Decorative
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


/**
 * Displays a welcome message and suggestions when no categories exist.
 */
@Composable
private fun EmptyStateView(
    modifier: Modifier = Modifier,
    onAddNewCategoryClick: () -> Unit
) {
    val suggestedCategories = listOf("Home", "Car", "School", "Shopping")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to PhoToDo!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Get started by creating a category for your photo to-dos.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Some suggestions to start:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Display suggested categories
        suggestedCategories.forEach { category ->
            Text(
                text = "• $category",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onAddNewCategoryClick) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text("Add New Category")
        }
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "Home Screen With Categories")
@Composable
fun HomeScreenWithCategoriesPreview() {
    // MaterialTheme { // Wrap with your app's theme
    HomeScreen(
        categories = listOf("Home", "Car", "School", "Shopping"),
        onCategoryClick = {},
        onAddNewCategoryClick = {}
    )
    // }
}

@Preview(showBackground = true, name = "Home Screen Empty State")
@Composable
fun HomeScreenEmptyPreview() {
    // MaterialTheme { // Wrap with your app's theme
    HomeScreen(
        categories = emptyList(),
        onCategoryClick = {},
        onAddNewCategoryClick = {}
    )
    // }
}