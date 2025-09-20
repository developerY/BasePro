package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlexibleBottomAppBarSimpleExample() {
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FlexibleBottomAppBarExample(modifier: Modifier = Modifier) {


    val icons =
        listOf(
            Icons.AutoMirrored.Filled.ArrowBack,
            Icons.AutoMirrored.Filled.ArrowForward,
            Icons.Filled.Add,
            Icons.Filled.Check,
            Icons.Filled.Edit,
            Icons.Filled.Favorite,
        )
    val items = listOf("Back", "Forward", "Add", "Check", "Edit", "Favorite")
    FlexibleBottomAppBar(
        contentPadding = PaddingValues(horizontal = 96.dp),
        horizontalArrangement = BottomAppBarDefaults.FlexibleFixedHorizontalArrangement,
    ) {
        AppBarRow(
            overflowIndicator = { menuState ->
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above
                        ),
                    tooltip = { PlainTooltip { Text("Overflow") } },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = {
                            if (menuState.isExpanded) {
                                menuState.dismiss()
                            } else {
                                menuState.show()
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Overflow")
                    }
                }
            }
        ) {
            items.forEachIndexed { index, item ->
                clickableItem(
                    onClick = { /* doSomething() */ },
                    icon = { Icon(icons[index], contentDescription = item) },
                    label = item,
                )
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