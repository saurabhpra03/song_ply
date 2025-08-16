package com.song.ply.di

import android.content.Context
import com.song.ply.framework.data.repository.playlist.PlaylistRepository
import com.song.ply.framework.data.repository.playlist.PlaylistRepositoryImpl
import com.song.ply.framework.data.repository.playlistSongs.PlaylistSongsRepository
import com.song.ply.framework.data.repository.playlistSongs.PlaylistSongsRepositoryImpl
import com.song.ply.framework.data.repository.songs.SongsRepository
import com.song.ply.framework.data.repository.songs.SongsRepositoryImpl
import com.song.ply.framework.database.dao.PlaylistDao
import com.song.ply.framework.database.dao.PlaylistSongsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun songsRepository(
        @ApplicationContext context: Context
    ): SongsRepository =
        SongsRepositoryImpl(context)

    @Provides
    @Singleton
    fun playlistRepository(
        @ApplicationContext context: Context,
        dao: PlaylistDao
    ): PlaylistRepository = PlaylistRepositoryImpl(context, dao)

    @Provides
    @Singleton
    fun playlistSongsRepository(
        @ApplicationContext context: Context,
        dao: PlaylistSongsDao
    ): PlaylistSongsRepository = PlaylistSongsRepositoryImpl(context, dao)
}