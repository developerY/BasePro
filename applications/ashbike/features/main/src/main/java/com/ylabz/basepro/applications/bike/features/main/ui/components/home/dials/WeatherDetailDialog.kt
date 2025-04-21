package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.applications.bike.features.main.R

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

@Preview
@Composable
fun WeatherDetailDialogPreview() {
    val weatherInfo = BikeWeatherInfo(
        windDegree = 180,
        windSpeed = 15.0,
        conditionText = "Sunny",
        conditionDescription = "Clear sky",
        conditionIcon = "sunny", temperature = 25.0, feelsLike = 26.0, humidity = 50)
    WeatherDetailDialog(weatherInfo = weatherInfo, onDismiss = {})
}