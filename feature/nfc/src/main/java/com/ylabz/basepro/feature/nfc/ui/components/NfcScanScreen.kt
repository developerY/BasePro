package com.ylabz.basepro.feature.nfc.ui.components

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun NfcScanScreen(
    isScanning: Boolean, // True when we want NFC scanning enabled
    onTagScanned: (Tag) -> Unit,
    onError: (String) -> Unit,
    onStopScan: () -> Unit
) {
    // Obtain the current Activity from the Composition.
    val activity = LocalContext.current as? Activity
    // Get the NFC adapter (may be null if device does not support NFC).
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(activity) }

    // Use 'isScanning' as a key so that when it changes the DisposableEffect re-runs.
    DisposableEffect(isScanning) {
        if (isScanning) {
            activity?.enableForegroundDispatch(nfcAdapter)
        }
        onDispose {
            if (isScanning) {
                activity?.disableForegroundDispatch(nfcAdapter)
            }
        }
    }

    // UI: Show scanning instructions and a Stop Scan button.
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scanning... Please tap an NFC tag.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStopScan) {
            Text("Stop Scan")
        }
    }
}


fun Activity.enableForegroundDispatch(nfcAdapter: NfcAdapter?) {
    if (nfcAdapter == null) {
        Log.w("NFC", "enableForegroundDispatch: NFC adapter is null. Cannot enable dispatch.")
        return
    }
    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, this::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) },
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    Log.d("NFC", "enableForegroundDispatch: Enabled NFC foreground dispatch for ${this.localClassName}")
}

fun Activity.disableForegroundDispatch(nfcAdapter: NfcAdapter?) {
    if (nfcAdapter == null) {
        Log.w("NFC", "disableForegroundDispatch: NFC adapter is null. Cannot disable dispatch.")
        return
    }
    nfcAdapter.disableForegroundDispatch(this)
    Log.d("NFC", "disableForegroundDispatch: Disabled NFC foreground dispatch for ${this.localClassName}")
}
