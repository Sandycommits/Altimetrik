package com.altimetrik.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.altimetrik.model.Album

@Dao
interface AlbumDao {
    @Query("SELECT * FROM album")
    fun getAll(): List<Album>

    @Insert
    fun insertAll(users: List<Album>)
}