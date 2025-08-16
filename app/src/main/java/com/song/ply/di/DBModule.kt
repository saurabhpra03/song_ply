package com.song.ply.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.song.ply.BuildConfig
import com.song.ply.framework.database.MyAppDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DBModule {

    @Provides
    fun myAppDB(@ApplicationContext context: Context): MyAppDB {
        return Room.databaseBuilder(
            context,
            MyAppDB::class.java,
            BuildConfig.DB_NAME
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun playListDao(db: MyAppDB) = db.playlistDao()

    @Provides
    fun songsDao(db: MyAppDB) = db.songsDao()
}