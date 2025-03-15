package com.ylabz.basepro.feature.nfc.ui

sealed class NfcEvent {
    object StartScan : NfcEvent()
    object Retry : NfcEvent()
}