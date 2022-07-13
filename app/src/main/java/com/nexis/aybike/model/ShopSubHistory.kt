package com.nexis.aybike.model

import com.google.firebase.firestore.FieldValue

data class ShopSubHistory(
    val userId: String = "",
    val shopId: String = "",
    val purchaseDocId: String = "",
    val purchaseDate: FieldValue = FieldValue.serverTimestamp()
)
