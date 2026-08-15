package com.shreeram.balloonpop.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.shreeram.balloonpop.R

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var popSoundId: Int = -1
    private var isLoaded = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        
        soundPool?.setOnLoadCompleteListener { _, _, _ ->
            isLoaded = true
        }

        popSoundId = soundPool?.load(context, R.raw.pop, 1) ?: -1
    }

    fun playPopSound() {
        if (isLoaded && popSoundId != -1) {
            soundPool?.play(popSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
