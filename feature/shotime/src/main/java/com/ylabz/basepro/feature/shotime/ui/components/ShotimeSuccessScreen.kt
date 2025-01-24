package com.ylabz.basepro.feature.shotime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.core.model.alarm.ProAlarm
import com.ylabz.basepro.core.model.shotime.ShotimeSessionData
import kotlin.random.Random

@Composable
fun ShotimeSuccessScreen(
    modifier: Modifier = Modifier,
    data: List<ShotimeSessionData>,
    setAlarm: (ProAlarm) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Background color can be adjusted
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data) { item ->
                Text(
                    text = item.shot,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = {
                // Add a sample alarm for demo
                val currentTime = System.currentTimeMillis() + 100 // 1 sec later
                val proAlarm = ProAlarm(id = Random.nextInt(), timeInMillis = currentTime, message = "Test Alarm")
                setAlarm(proAlarm)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add ShotAlarm")
        }
    }
}
