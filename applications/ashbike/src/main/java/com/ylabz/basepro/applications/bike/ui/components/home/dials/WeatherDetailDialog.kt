package com.ylabz.basepro.applications.bike.ui.components.home.dials


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo

import com.ylabz.basepro.applications.bike.R


// 4) Detail dialog with extra stats
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailDialog(
    weatherInfo: BikeWeatherInfo,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.weather_details_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(
                    R.string.temperature_format,
                    weatherInfo.temperature?.toInt() ?: 0
                ))
                Text(stringResource(
                    R.string.feels_like_format,
                    weatherInfo.feelsLike?.toInt() ?: 0
                ))
                Text(stringResource(
                    R.string.humidity_format,
                    weatherInfo.humidity ?: 0
                ))
                Text(stringResource(
                    R.string.wind_format,
                    (weatherInfo.windSpeed).toInt(),
                    weatherInfo.windDegree.toInt()
                ))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok), fontWeight = FontWeight.Bold)
            }
        }
    )
}