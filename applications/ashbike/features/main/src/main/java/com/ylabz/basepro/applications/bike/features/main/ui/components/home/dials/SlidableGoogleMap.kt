package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.Canvas // Added
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text // Import for potential text drawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset // Added
import androidx.compose.ui.graphics.Color // Added
import androidx.compose.ui.graphics.PathEffect // For dashed line if needed, or remove
import androidx.compose.ui.graphics.drawscope.Stroke // Added
import androidx.compose.ui.platform.LocalDensity // Added
import androidx.compose.ui.text.rememberTextMeasurer // For drawing text
import androidx.compose.ui.text.style.TextAlign // For drawing text
import androidx.compose.ui.text.TextStyle // For drawing text
import androidx.compose.ui.text.drawText // For drawing text
import androidx.compose.ui.unit.dp // Added
import androidx.compose.ui.unit.sp // For text size
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.location.GpsFix // Import GpsFix

@Composable
fun SlidableGoogleMap(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success,
    onClose: () -> Unit,
    showMapContent: Boolean = true 
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp), 
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large 
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (showMapContent) {
                val currentLocation = remember(uiState.bikeData.location) {
                    uiState.bikeData.location ?: LatLng(0.0, 0.0)
                }
                var isMapReady by remember { mutableStateOf(false) }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
                }
                val markerState = rememberMarkerState(position = currentLocation)

                LaunchedEffect(currentLocation, isMapReady) {
                    if (isMapReady) {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(currentLocation, 15f),
                            durationMs = 700
                        )
                        markerState.position = currentLocation
                    }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = { isMapReady = true }
                ) {
                    Marker(
                        state = markerState, 
                        title = "Current Location"
                    )
                }
            } else {
                // Fallback UI: Green Screen with Path
                val fixes = uiState.bikeData.ridePath ?: emptyList() // Corrected line
                val fallbackBackgroundColor = Color.Green.copy(alpha = 0.3f)
                val pathColor = Color.White
                val pathStrokeWidth = with(LocalDensity.current) { 2.dp.toPx() }
                val insetPx = with(LocalDensity.current) { 16.dp.toPx() }
                val textMeasurer = rememberTextMeasurer()

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = fallbackBackgroundColor)

                    if (fixes.size >= 2) {
                        val minLat = fixes.minOf { it.lat }
                        val maxLat = fixes.maxOf { it.lat }
                        val minLng = fixes.minOf { it.lng }
                        val maxLng = fixes.maxOf { it.lng }

                        val latRange = (maxLat - minLat).takeIf { it > 0.00001 } ?: 0.001
                        val lngRange = (maxLng - minLng).takeIf { it > 0.00001 } ?: 0.001

                        val canvasWidth = size.width - 2 * insetPx
                        val canvasHeight = size.height - 2 * insetPx

                        val project: (Double, Double) -> Offset = { lat, lng ->
                            val x = insetPx + (((lng - minLng) / lngRange) * canvasWidth).toFloat()
                            val y = insetPx + (((maxLat - lat) / latRange) * canvasHeight).toFloat()
                            Offset(x.coerceIn(insetPx, size.width - insetPx), y.coerceIn(insetPx, size.height - insetPx))
                        }

                        val pathPoints = fixes.map { project(it.lat, it.lng) }

                        for (i in 0 until pathPoints.size - 1) {
                            drawLine(
                                color = pathColor,
                                start = pathPoints[i],
                                end = pathPoints[i + 1],
                                strokeWidth = pathStrokeWidth,
                                // Optional: dashed line, remove if solid line is preferred
                                // pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) 
                            )
                        }
                    } else {
                        val textLayoutResult = textMeasurer.measure(
                            text = "No path data to display",
                            style = TextStyle(color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Center),
                            constraints = androidx.compose.ui.unit.Constraints(maxWidth = (size.width - 2 * insetPx).toInt())
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                x = (size.width - textLayoutResult.size.width) / 2,
                                y = (size.height - textLayoutResult.size.height) / 2
                            )
                        )
                    }
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close Map",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
