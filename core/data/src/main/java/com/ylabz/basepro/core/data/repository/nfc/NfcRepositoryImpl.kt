package com.ylabz.basepro.core.data.repository.nfc

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
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
        // Reading logic: Read NDEF data from the tag.
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
                try { ndef.close() } catch (ignored: Exception) { }
            }
        } else {
            _scannedDataFlow.tryEmit("NDEF not supported on this tag")
        }
    }

    override fun clearScannedData() {
        _scannedDataFlow.resetReplayCache()
    }

    override suspend fun writeTag(tag: Tag, text: String): Boolean {
        val ndef = Ndef.get(tag) ?: return false  // Tag doesn't support NDEF.
        try {
            ndef.connect()
            if (!ndef.isWritable) return false
            // Create a text record with language code "en"
            val record = NdefRecord.createTextRecord("en", text)
            val message = NdefMessage(arrayOf(record))
            if (ndef.maxSize < message.toByteArray().size) return false
            ndef.writeNdefMessage(message)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try { ndef.close() } catch (ignored: Exception) { }
        }
    }
}

