package com.ylabz.basepro.ashbike.mobile.features.glass.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale

/**
 * Robust TTS Helper for Google AI Glasses.
 * Handles lifecycle, audio focus, and provides completion callbacks.
 */
class TTSHelper(context: Context) : DefaultLifecycleObserver {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isReady = false
    
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val pendingMessages = mutableListOf<Triple<String, Boolean, (() -> Unit)?>>()
    private val callbacks = mutableMapOf<String, () -> Unit>()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        if (tts == null) {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    setupEngine()
                } else {
                    Log.e("TTSHelper", "Initialization failed with status: $status")
                }
            }
        }
    }

    private fun setupEngine() {
        tts?.apply {
            val result = setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSHelper", "Language not supported")
            } else {
                // Optimization for Glasses: Use specific audio attributes for navigation/guidance
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                setAudioAttributes(audioAttributes)

                setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        callbacks.remove(utteranceId)?.invoke()
                    }
                    @Deprecated("Deprecated in Java", ReplaceWith("onError(utteranceId, errorCode)"))
                    override fun onError(utteranceId: String?) {
                        callbacks.remove(utteranceId)
                    }
                    override fun onError(utteranceId: String?, errorCode: Int) {
                        callbacks.remove(utteranceId)
                    }
                })
                
                isReady = true
                processPendingMessages()
            }
        }
    }

    /**
     * Speaks the text. If the engine isn't ready, it queues the message.
     * @param text The string to read aloud.
     * @param flush If true, interrupts current speech. If false, adds to queue.
     * @param onComplete Optional callback invoked when this specific utterance finishes.
     */
    fun speak(text: String, flush: Boolean = true, onComplete: (() -> Unit)? = null) {
        if (!isReady) {
            synchronized(pendingMessages) {
                if (flush) pendingMessages.clear()
                pendingMessages.add(Triple(text, flush, onComplete))
            }
            return
        }

        val utteranceId = "GH_${System.currentTimeMillis()}"
        if (onComplete != null) {
            callbacks[utteranceId] = onComplete
        }

        requestAudioFocus()

        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, utteranceId)
    }

    private fun requestAudioFocus() {
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build())
            .build()
        audioManager.requestAudioFocus(request)
    }

    private fun processPendingMessages() {
        synchronized(pendingMessages) {
            pendingMessages.forEach { (text, flush, cb) ->
                speak(text, flush, cb)
            }
            pendingMessages.clear()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
        callbacks.clear()
    }
}
