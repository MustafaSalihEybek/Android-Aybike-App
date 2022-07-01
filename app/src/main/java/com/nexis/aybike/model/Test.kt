package com.nexis.aybike.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Test(
    val testId: String = "",
    val testImageUrl: String = "",
    val testTitle: String = "",
    val testLikeAmount: Int = 0,
    val testViewAmount: Int = 0
) : Parcelable
