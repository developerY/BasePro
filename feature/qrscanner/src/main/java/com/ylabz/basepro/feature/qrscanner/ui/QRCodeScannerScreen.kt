package com.ylabz.basepro.feature.qrscanner.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.Barcode as GmsBarcode  // In some cases, the Barcode class may be directly imported from ML Kit.
import com.google.mlkit.vision.barcode.GmsBarcodeScanner
import com.google.mlkit.vision.barcode.GmsBarcodeScannerOptions

@Composable
fun QRCodeScannerScreen() {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf("No result") }

    // Build options for scanning QR and Aztec codes.
    // These options come from the play-services-code-scanner library.
    val options = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
            .build()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            // Get the scanner client from Google Play Services Code Scanner
            val scanner: GmsBarcodeScanner = GmsBarcodeScanner.getClient(context, options)
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    scanResult = barcode.rawValue ?: "No value"
                }
                .addOnFailureListener { exception ->
                    scanResult = "Error: ${exception.message}"
                }
        }) {
            Text("Scan QR Code")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Result: $scanResult")
    }
}
