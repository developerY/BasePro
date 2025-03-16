package com.ylabz.basepro.feature.nfc.ui.components

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun NfcScanScreen(
    onTagScanned: (Tag) -> Unit,
    onError: (String) -> Unit
) {
    // Obtain the current Activity from the Composition
    val activity = LocalContext.current as? Activity
    // Get the NFC adapter (may be null if device does not support NFC)
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(activity) }

    // Enable/disable NFC scanning as this Composable enters/leaves the composition
    DisposableEffect(Unit) {
        // Enable scanning
        activity?.enableForegroundDispatch(nfcAdapter)

        // On dispose, disable scanning
        onDispose {
            activity?.disableForegroundDispatch(nfcAdapter)
        }
    }

    // Now your UI can simply say "Waiting for NFC tag..."
    // or display any additional scanning instructions
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Waiting for an NFC tag...\nTap your NFC tag now.")
    }
}

fun Activity.enableForegroundDispatch(nfcAdapter: NfcAdapter?) {
    if (nfcAdapter == null) return
    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, this::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) },
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
}

fun Activity.disableForegroundDispatch(nfcAdapter: NfcAdapter?) {
    nfcAdapter?.disableForegroundDispatch(this)
}

