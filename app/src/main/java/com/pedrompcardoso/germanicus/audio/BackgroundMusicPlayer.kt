package com.pedrompcardoso.germanicus.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

object BackgroundMusicPlayer {
    private const val TAG = "BackgroundMusicPlayer"
    private const val MUSIC_VOLUME = 0.35f

    private var mediaPlayer: MediaPlayer? = null
    private var musicResources: List<Int> = emptyList()
    private var currentTrackIndex = 0
    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) {
            return
        }

        musicResources = loadRawResourceIds(context).shuffled()
        initialized = true
    }

    fun play(context: Context) {
        initialize(context.applicationContext)

        if (musicResources.isEmpty()) {
            return
        }

        if (mediaPlayer == null) {
            prepareTrack(context.applicationContext, currentTrackIndex)
        }

        runCatching {
            mediaPlayer?.start()
        }.onFailure { error ->
            Log.w(TAG, "Unable to start background music", error)
            releaseCurrentPlayer()
        }
    }

    fun pause() {
        runCatching {
            mediaPlayer
                ?.takeIf { it.isPlaying }
                ?.pause()
        }.onFailure { error ->
            Log.w(TAG, "Unable to pause background music", error)
        }
    }

    fun release() {
        releaseCurrentPlayer()
        musicResources = emptyList()
        currentTrackIndex = 0
        initialized = false
    }

    private fun prepareTrack(context: Context, trackIndex: Int) {
        releaseCurrentPlayer()

        val resourceId = musicResources.getOrNull(trackIndex) ?: return
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                context.resources.openRawResourceFd(resourceId).use { descriptor ->
                    setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                }
                prepare()
                setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
                isLooping = musicResources.size == 1
                setOnCompletionListener {
                    playNextTrack(context)
                }
            }
        }.onFailure { error ->
            Log.w(TAG, "Unable to prepare background music", error)
            releaseCurrentPlayer()
        }
    }

    private fun playNextTrack(context: Context) {
        if (musicResources.isEmpty()) {
            return
        }

        currentTrackIndex = (currentTrackIndex + 1) % musicResources.size
        prepareTrack(context, currentTrackIndex)
        mediaPlayer?.start()
    }

    private fun releaseCurrentPlayer() {
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun loadRawResourceIds(context: Context): List<Int> {
        return runCatching {
            Class.forName("${context.packageName}.R\$raw")
                .fields
                .mapNotNull { field ->
                    field.getInt(null).takeIf { it != 0 }
                }
        }.getOrElse {
            emptyList()
        }
    }
}
