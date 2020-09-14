package com.altimetrik.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseData(
    @SerializedName("resultCount") var resultCount: Int?,
    @SerializedName("results") var albums: List<Album>
) : Serializable
