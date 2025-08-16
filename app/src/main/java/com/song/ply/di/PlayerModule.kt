package com.song.ply.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlayerModule {

    @Provides
    @Singleton
    fun providesExoPlayer(@ApplicationContext context: Context): ExoPlayer =
        ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun providesMediaSession(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): MediaSession = MediaSession.Builder(context, exoPlayer).build()
}