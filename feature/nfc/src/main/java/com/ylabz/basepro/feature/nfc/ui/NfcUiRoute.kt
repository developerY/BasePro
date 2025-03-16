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
        is NfcUiState.Loading -> {
            LoadingScreen()
        }
        is NfcUiState.Error -> {
            ErrorScreen(
                message = (uiState as NfcUiState.Error).message,
                onRetry = { viewModel.onEvent(NfcReadEvent.Retry) }
            )
        }
        is NfcUiState.NfcNotSupported,
        is NfcUiState.NfcDisabled,
        is NfcUiState.WaitingForTag,
        is NfcUiState.TagScanned,
        is NfcUiState.Idle -> {
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
fun NfcReaderRouteOld(
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: NfcViewModel = hiltViewModel()
) {
    // Collect the current UI state from our ViewModel
    val uiState = viewModel.uiState.collectAsState().value

    // (Optional) We can also collect other flows, e.g. scanning state, error messages, etc.
    // val scanningState by viewModel.scanningState.collectAsState()

    // Provide some container for the UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Render different screens based on uiState
        when (uiState) {
            is NfcUiState.NfcNotSupported -> {
                NfcNotSupportedScreen(
                    onRetry = { viewModel.onEvent(NfcReadEvent.Retry) }
                )
            }

            is NfcUiState.NfcDisabled -> {
                NfcDisabledScreen(
                    onEnableNfc = { /*viewModel.onEvent(NfcReadEvent.EnableNfcRead)*/ }
                )
            }

            is NfcUiState.WaitingForTag -> {
                NfcWaitingScreen(
                    // Maybe a button or instructions to say "Tap your NFC tag now."
                )
            }

            is NfcUiState.TagScanned -> {
                NfcTagScannedScreen(
                    tagInfo = uiState.tagInfo,
                    onDone = { navTo("SomeNextScreen") }
                )
            }

            is NfcUiState.Loading -> {
                LoadingScreen()
            }

            is NfcUiState.Error -> {
                ErrorScreen(
                    message = uiState.message,
                    onRetry = { viewModel.onEvent(NfcReadEvent.Retry) }
                )
            }

            NfcUiState.Idle -> TODO()
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
