//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.ylabz.basepro.feature.maps.ui.components.ErrorOverlay

@Composable
fun MapContent(
    directions: String,
    paddingValues: PaddingValues,
    isError: Boolean = false,
    errorMessage: String = "",
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Map and directions content in Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = directions,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(1.35, 103.87), 10f)
                }
            )
        }

        // Transparent error overlay (appears above Column and map)
        if (isError) {
            ErrorOverlay(
                errorMessage = errorMessage,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center) // Center align overlay within Box
                    .background(Color.Black.copy(alpha = 0.5f)) // Ensure semi-transparent background
            )
        }
    }
}

/*
/*@Preview(showBackground = true)
@Composable
fun MapContentPreview() {
    MapContent(
        directions = "Head north on Main Street and turn left at the second traffic light.",
        paddingValues = PaddingValues(16.dp),
        isError = true, // To show error overlay in preview
        errorMessage = "Network error. Please try again.",
        onRetry = { println("Retry clicked!") }, // Mock retry callback
        modifier = Modifier.fillMaxSize()
    )
}
*/