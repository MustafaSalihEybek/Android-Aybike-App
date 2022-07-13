package com.nexis.aybike.util

import com.google.firebase.Timestamp
import com.nexis.aybike.model.SubCategory
import kotlinx.android.synthetic.main.aybike_action_bar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object AppUtils {
    fun shuffleTheAnswers(answers: ArrayList<String>) : ArrayList<String> {
        val aSize: Int = answers.size
        val aList: ArrayList<String> = ArrayList()
        var aRnd: Int = 0

        for (q in 0 until aSize){
            aRnd = (0 until answers.size).random()
            aList.add(answers.get(aRnd))
            answers.removeAt(aRnd)
        }

        return aList
    }

    fun shuffleTheAnswersAndImages(answers: ArrayList<String>, images: ArrayList<String>) : ArrayList<ArrayList<String>> {
        val dSize: Int = answers.size
        val aList: ArrayList<String> = ArrayList()
        val iList: ArrayList<String> = ArrayList()
        val dList: ArrayList<ArrayList<String>> = ArrayList()
        var dRnd: Int = 0

        for (q in 0 until dSize){
            dRnd = (0 until answers.size).random()

            aList.add(answers.get(dRnd))
            iList.add(images.get(dRnd))

            answers.removeAt(dRnd)
            images.removeAt(dRnd)
        }

        dList.add(aList)
        dList.add(iList)

        return dList
    }

    fun getEditedSubCategoryList(subCategoryList: ArrayList<SubCategory>) : ArrayList<SubCategory> {
        val categoryList: ArrayList<SubCategory> = subCategoryList
        var firstSubCategory: SubCategory

        for (c in subCategoryList.indices){
            if (subCategoryList.get(c).categoryNumber == 1){
                firstSubCategory = categoryList.get(0)
                categoryList[0] = categoryList[c]
                categoryList[c] = firstSubCategory
            }
        }

        return categoryList
    }

    fun getEditedNumbers(numbers: Int) : String {
        if (numbers >= 1000){
            val firstDigits = (numbers / 1000)
            val lastDigits = (numbers % 1000)

            if (lastDigits >= 100)
                return "$firstDigits.${(lastDigits / 100)}b"
            else
                return "${firstDigits}b"
        }

        return "$numbers"
    }

    fun getFullDateWithString() : String {
        val timeNowFromFirebase = Date(Timestamp.now().seconds * 1000)
        val fullDate = SimpleDateFormat("dd/M/yyyy")
        val fullDateStr = fullDate.format(timeNowFromFirebase)

        return fullDateStr
    }

    fun getAddedDayTime(dayNumber: Int) : String {
        val simpleDate: SimpleDateFormat = SimpleDateFormat("dd/M/yyyy")
        val calendar: Calendar = Calendar.getInstance()

        try {
            calendar.time = simpleDate.parse(AppUtils.getFullDateWithString())
        } catch (e: ParseException){
            e.printStackTrace()
        }

        calendar.add(Calendar.DATE, dayNumber)
        val simpleDate2: SimpleDateFormat = SimpleDateFormat("dd/M/yyyy")

        return simpleDate2.format(calendar.time)
    }

    fun increaseCorrectAndWrongAmount(isWrong: Boolean){
        if (!isWrong){
            Singleton.correctAndWrongAmountTuple = Pair(
                Singleton.correctAndWrongAmountTuple.first + 1,
                Singleton.correctAndWrongAmountTuple.second
            )
        }else{
            Singleton.correctAndWrongAmountTuple = Pair(
                Singleton.correctAndWrongAmountTuple.first,
                Singleton.correctAndWrongAmountTuple.second + 1
            )
        }
    }
}