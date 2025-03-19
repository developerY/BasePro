package com.ylabz.basepro.core.data.repository.nfc

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import android.nfc.Tag
import dagger.hilt.android.qualifiers.ApplicationContext


@Singleton
class NfcRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NfcRepository {

    // Use a SharedFlow so multiple collectors get the same result.
    private val _scannedDataFlow = MutableSharedFlow<String>(replay = 1)
    override val scannedDataFlow: Flow<String> = _scannedDataFlow

    /**
     * Checks if the device supports NFC.
     */
    override fun isNfcSupported(): Boolean {
        return NfcAdapter.getDefaultAdapter(context) != null
    }

    /**
     * Checks if NFC is enabled on the device.
     */
    override fun isNfcEnabled(): Boolean {
        return NfcAdapter.getDefaultAdapter(context)?.isEnabled ?: false
    }

    /**
     * Called when a new NFC tag is detected. Attempts to read the tag as NDEF and emits
     * the result (or an error message) to the shared flow.
     */
    override fun onTagScanned(tag: Tag) {
        // (Existing logic for reading the tag)
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            try {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                val result = ndefMessage?.records?.joinToString(separator = "\n") { record ->
                    record.payload.decodeToString().trim()
                } ?: "Empty Tag"
                _scannedDataFlow.tryEmit(result)
            } catch (e: Exception) {
                _scannedDataFlow.tryEmit("Error reading tag: ${e.message}")
            } finally {
                try {
                    ndef.close()
                } catch (_: Exception) { }
            }
        } else {
            _scannedDataFlow.tryEmit("NDEF not supported on this tag")
        }
    }

    override fun clearScannedData() {
        _scannedDataFlow.resetReplayCache()
    }
}
