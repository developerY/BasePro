package com.ylabz.basepro.feature.ble.ui.components

////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.DeviceService

// Composable for GATT Services Carousel
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GattServicesCarousel(
    modifier: Modifier = Modifier,
    services: List<DeviceService>,
    readBat: () -> Unit
) {
    Column(modifier = modifier) {
        Button(
            onClick = readBat,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Read Battery Level")
        }

        Text(
            text = "GATT Services",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                // State to manage whether the card is flipped
                var isFlipped by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .width(240.dp)
                        .clickable { isFlipped = !isFlipped }
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    AnimatedContent(
                        targetState = isFlipped,
                        transitionSpec = {
                            fadeIn() with fadeOut()
                        }
                    ) { flipped ->
                        if (flipped) {
                            BackOfCard(service)
                        } else {
                            FrontOfCard(service)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FrontOfCard(service: DeviceService) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = service.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "UUID: ${service.uuid}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun BackOfCard(service: DeviceService) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Characteristics:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        service.characteristics.forEach { characteristic ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = characteristic.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "Value: ${characteristic.value}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/*
/*@Preview(showBackground = true)
@Composable
fun GattServicesPreview() {
    val sampleServices = listOf(
        DeviceService(
            uuid = "1234",
            name = "Heart Rate Service",
            characteristics = listOf(
                DeviceCharacteristic("5678", "Heart Rate Measurement", true, false, true, "80 bpm"),
                DeviceCharacteristic("9101", "Body Sensor Location", true, false, false, "Chest")
            )
        ),
        DeviceService(
            uuid = "2234",
            name = "Battery Service",
            characteristics = listOf(
                DeviceCharacteristic("6678", "Battery Level", true, false, false, "95%")
            )
        )
    )
    GattServicesCarousel(
        services = sampleServices,
        readBat = { /* Placeholder for reading battery */ }
    )
}
*/