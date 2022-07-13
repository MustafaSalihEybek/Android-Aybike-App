package com.nexis.aybike.model

data class User(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userGender: String = "",
    val userBirthday: String = "",
    val userCountry: String = "",
    val userCity: String = "",
    val userPoint: Float = 0F
)
