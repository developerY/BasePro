package com.ylabz.basepro.feature.heatlh.ui.components.notused

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DashboardScreen()
            }
        }
    }
}*/

@Composable
fun DashboardScreen() {
    Scaffold(
        topBar = { TopBar() },
        //bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        DashboardContent(Modifier.padding(paddingValues))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Dashboard") },
        //backgroundColor = MaterialTheme.colors.primary
    )
}

@Composable
fun DashboardContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // If there's a high/low alert, display an alert banner
        val showAlert = false // Replace with your condition logic
        if (showAlert) {
            AlertBanner(message = "High Glucose Alert!")
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Top Section: Current BG and Trend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Current BG: 5.8 mmol/L", style = MaterialTheme.typography.headlineLarge)
                Text(text = "Trend: Rising", style = MaterialTheme.typography.bodySmall)
            }
            // Replace with your custom icon/resource if available
            /*Icon(
                imageVector = Icons.Default.Home, // Placeholder icon; use your trend icon
                contentDescription = "Trend Indicator",
                modifier = Modifier.size(32.dp)
            )*/
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Gauge or Ring indicating Time in Range
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "70%\nin range",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Key Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeyStatTile(title = "Time in Range", value = "72%")
            KeyStatTile(title = "Active Insulin", value = "0.15 U")
            KeyStatTile(title = "Carbs On Board", value = "20 g")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Recent Trends Widget (Sparkline Chart Placeholder)
        RecentTrendsChart()
        Spacer(modifier = Modifier.height(16.dp))
        // Quick Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(label = "Log BG") { /* Handle action */ }
            QuickActionButton(label = "Add Meal") { /* Handle action */ }
            QuickActionButton(label = "History") { /* Handle action */ }
            QuickActionButton(label = "Settings") { /* Handle action */ }
        }
    }
}

@Composable
fun AlertBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(8.dp)
    ) {
        Text(text = message, color = Color.White)
    }
}

@Composable
fun KeyStatTile(title: String, value: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp),
        //elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineLarge)
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun RecentTrendsChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        //elevation = 4.dp
    ) {
        // Placeholder for sparkline chart. You can replace this with a Canvas-based custom chart.
        Box(contentAlignment = Alignment.Center) {
            Text(text = "Recent 6h Trend Chart (sparkline)")
        }
    }
}

@Composable
fun QuickActionButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = label)
    }
}
/*
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}
*/
