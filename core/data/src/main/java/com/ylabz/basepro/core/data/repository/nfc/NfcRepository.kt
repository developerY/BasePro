package com.ylabz.basepro.core.data.repository.nfc


import android.nfc.Tag
import android.nfc.tech.Ndef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

interface NfcRepository {
    /**
     * Emits the scanned NFC data (e.g. as text) whenever a new tag is read.
     */
    val scannedDataFlow: Flow<String>

    /**
     * Called when a new NFC tag is scanned.
     */
    fun onTagScanned(tag: Tag)
}

