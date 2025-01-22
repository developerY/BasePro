package com.ylabz.basepro.feature.alarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorScreen(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black), // Background color can be adjusted
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error: $message",
                color = Color.Red,
                fontSize = 18.sp,
                //fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            onRetry?.let {
                Button(
                    onClick = it,
                    colors = ButtonDefaults.buttonColors(
                        //backgroundColor = Color.Red
                    )
                ) {
                    Text(
                        text = "Retry",
                        color = Color.White,
                        fontSize = 18.sp,
                        //fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                        //fontSize = 16.sp,
                        //fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
