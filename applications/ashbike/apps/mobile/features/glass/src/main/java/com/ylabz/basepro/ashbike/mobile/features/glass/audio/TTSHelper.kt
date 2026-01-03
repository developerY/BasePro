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

class TTSHelper(context: Context) : DefaultLifecycleObserver {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isReady = false

    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val pendingMessages = mutableListOf<Triple<String, Boolean, (() -> Unit)?>>()
    private val callbacks = mutableMapOf<String, () -> Unit>()

    // Hold the focus request object to release it cleanly later
    private var focusRequest: AudioFocusRequest? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        if (tts == null) {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    setupEngine()
                } else {
                    Log.e("TTSHelper", "Init failed: $status")
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
                // 1. Set Audio Attributes directly (Clean API)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                setAudioAttributes(audioAttributes)

                setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        callbacks.remove(utteranceId)?.invoke()
                        // Optional: abandonAudioFocus() here if you want music to return immediately
                    }

                    // 2. Minimal Required Override for Abstract Class (Keep empty)
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        // This method is abstract in the parent class, so we MUST override it.
                        // But we don't need to put logic here.
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        callbacks.remove(utteranceId)
                        Log.e("TTSHelper", "Error ($errorCode): $utteranceId")
                    }
                })

                isReady = true
                processPendingMessages()
            }
        }
    }

    fun speak(text: String, flush: Boolean = true, onComplete: (() -> Unit)? = null) {
        if (!isReady) {
            synchronized(pendingMessages) {
                if (flush) pendingMessages.clear()
                pendingMessages.add(Triple(text, flush, onComplete))
            }
            return
        }

        val utteranceId = "MSG_${System.currentTimeMillis()}"
        if (onComplete != null) {
            callbacks[utteranceId] = onComplete
        }

        requestAudioFocus()

        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, utteranceId)
    }

    // 3. CLEAN AUDIO FOCUS (No 'if' checks, No suppressions)
    private fun requestAudioFocus() {
        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setOnAudioFocusChangeListener { /* Handle ducking/pausing if needed */ }
            .build()

        audioManager.requestAudioFocus(focusRequest!!)
    }

    private fun abandonAudioFocus() {
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
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
        abandonAudioFocus()
    }
}