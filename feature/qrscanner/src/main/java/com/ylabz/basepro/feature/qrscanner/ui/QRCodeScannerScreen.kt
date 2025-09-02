package com.ylabz.basepro.feature.qrscanner.ui

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.ylabz.basepro.feature.qrscanner.R


@Composable
fun QRCodeScannerScreen() {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf(context.getString(R.string.qr_scanner_initial_result)) }

    // Build options for scanning QR and Aztec codes
    val options = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            // NOTE: The correct call for Play Services Code Scanner is GmsBarcodeScanning.getClient
            val scanner = GmsBarcodeScanning.getClient(context, options)
            val result: Task<Barcode> = scanner.startScan()
            result.addOnSuccessListener { barcode ->
                // barcode.rawValue contains the scanned text
                scanResult =
                    barcode.rawValue ?: context.getString(R.string.qr_scanner_no_value_found)
            }.addOnFailureListener { exception ->
                scanResult = context.getString(
                    R.string.qr_scanner_error_prefix,
                    exception.message ?: "Unknown error"
                )
            }
        }) {
            Text(stringResource(id = R.string.qr_scanner_button_text))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.qr_scanner_result_prefix, scanResult))
    }
}

/*
@Preview
@Composable
fun QRCodeScannerScreenPreview() {
    QRCodeScannerScreen()
}
*/