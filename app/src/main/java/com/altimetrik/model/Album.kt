package com.altimetrik.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class Album(
    @PrimaryKey val trackId: Int,
    @ColumnInfo(name = "artistName")@SerializedName("artistName") var artistName: String,
    @ColumnInfo(name = "collectionName")@SerializedName("collectionName") var collectionName: String?,
    @ColumnInfo(name = "trackName")@SerializedName("trackName") var trackName: String?,
    @ColumnInfo(name = "collectionPrice")@SerializedName("collectionPrice") var collectionPrice: Double?,
    @ColumnInfo(name = "releaseDate")@SerializedName("releaseDate") var releaseDate: String?,
    @ColumnInfo(name = "artworkUrl100")@SerializedName("artworkUrl100") var artworkUrl100: String?,
    var selected: Boolean = false
) : Serializable