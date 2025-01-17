package com.jpobi.xkcd_viewer

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName="comic")
data class Comic (
    val alt: String,
    val day: String,
    val img: String,
    val link: String,
    val month: String,
    val news: String,
    @PrimaryKey
    val num: Int,
    val safe_title: String,
    val title: String,
    val transcript: String,
    val year: String,
    var isFav: Boolean =false
    ) : Parcelable
