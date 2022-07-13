package com.nexis.aybike.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubCategory(
    val categoryId: String = "",
    val subCategoryId: String = "",
    val subCategoryName: String = "",
    val categoryNumber: Int = 1
) : Parcelable
