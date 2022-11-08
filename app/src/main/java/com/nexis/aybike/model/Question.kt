package com.nexis.aybike.model

data class Question(
    val questionId: String = "",
    val questionContent: String = "",
    val questionCorrectAnswer: String = "",
    val questionPoint: Int = 2,
    val questionType: Int = 1,
    val questionAnswers: ArrayList<String> = ArrayList(),
    val questionImages: ArrayList<String> = ArrayList(),
    val questionCorrectChooseList: ArrayList<String> = ArrayList(),
    val questionTestId: String = "",
    val questionsSubCategoryId: String = ""
)
