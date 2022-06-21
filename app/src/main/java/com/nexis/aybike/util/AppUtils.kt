package com.nexis.aybike.util

import kotlinx.android.synthetic.main.aybike_action_bar.*

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
}