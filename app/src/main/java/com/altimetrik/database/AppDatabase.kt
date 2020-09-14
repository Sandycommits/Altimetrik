package com.altimetrik.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.altimetrik.model.Album

@Database(entities = arrayOf(Album::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
}