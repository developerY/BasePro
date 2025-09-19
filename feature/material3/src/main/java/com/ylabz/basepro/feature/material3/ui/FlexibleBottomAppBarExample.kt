package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlexibleBottomAppBarExample() {
    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            FlexibleBottomAppBar(
                scrollBehavior = scrollBehavior,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                content = {
                    IconButton(onClick = { /* Home */ }) { Icon(Icons.Filled.Home, "Home") }
                    IconButton(onClick = { /* Search */ }) { Icon(Icons.Filled.Search, "Search") }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* Profile */ }) { Icon(Icons.Filled.Person, "Profile") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(10) {
                Text("Item #$it", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun FlexibleBottomAppBarExamplePreview() {
    FlexibleBottomAppBarExample()
}