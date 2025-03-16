package com.ylabz.basepro.feature.nfc.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.nfc.ui.components.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.LoadingScreen
import com.ylabz.basepro.feature.nfc.ui.components.NfcAppScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcDisabledScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcNotSupportedScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcTagScannedScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcWaitingScreen



@Composable
fun NfcUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: NfcViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is NfcUiState.Error -> {
            ErrorScreen(
                message = (uiState as NfcUiState.Error).message,
                onRetry = { viewModel.onEvent(NfcReadEvent.Retry) }
            )
        }
        is NfcUiState.Loading -> {
            LoadingScreen()
        }
        // For all other states, show the main NfcAppScreen
        is NfcUiState.NfcNotSupported,
        is NfcUiState.NfcDisabled,
        is NfcUiState.Stopped,       // <-- Newly added (replaces Idle)
        is NfcUiState.WaitingForTag,
        is NfcUiState.TagScanned -> {
            NfcAppScreen(
                modifier = modifier,
                uiState = uiState,
                navTo = navTo,
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }
    }
}

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


@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black), // Background color can be adjusted
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}
