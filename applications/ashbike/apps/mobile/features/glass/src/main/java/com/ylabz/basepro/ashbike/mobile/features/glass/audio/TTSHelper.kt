package com.ylabz.basepro.ashbike.mobile.features.glass.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSHelper(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        // Initialize the standard Android TTS engine
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                } else {
                    isReady = true
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    /**
     * Speaks the text.
     * @param text The string to read aloud.
     * @param flush If true, interrupts current speech. If false, adds to queue.
     */
    fun speak(text: String, flush: Boolean = true) {
        if (!isReady) return

        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD

        // "utteranceId" is optional but good for tracking completion
        tts?.speak(text, queueMode, null, "ASHBIKE_MSG")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}