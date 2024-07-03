package com.jpobi.xkcd_viewer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comic (
    val alt: String,
    val day: String,
    val img: String,
    val link: String,
    val month: String,
    val news: String,
    val num: Int,
    val safe_title: String,
    val title: String,
    val transcript: String,
    val year: String
    ) : Parcelable
