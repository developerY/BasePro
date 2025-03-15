package com.ylabz.basepro.core.data.repository.nfc

import android.nfc.tech.Ndef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import android.nfc.Tag


@Singleton
class NfcRepositoryImpl @Inject constructor() : NfcRepository {

    // Use a SharedFlow so multiple collectors get the same result.
    private val _scannedDataFlow = MutableSharedFlow<String>(replay = 1)
    override val scannedDataFlow: Flow<String> = _scannedDataFlow

    override fun onTagScanned(tag: Tag) {
        // Attempt to read the tag as NDEF.
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
}