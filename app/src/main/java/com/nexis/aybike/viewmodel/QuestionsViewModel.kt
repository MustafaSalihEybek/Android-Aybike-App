package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.TestHistory
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.viewmodel.base.BaseViewModel

class QuestionsViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var questions: ArrayList<Question>

    val questionsList = MutableLiveData<ArrayList<Question>>()
    val errorMessage = MutableLiveData<String>()

    fun getQuestions(subCategoryId: String, testId: String){
        FirebaseUtils.mQuery = FirebaseUtils.mFireStore.collection("SubCategories")
            .document(subCategoryId).collection("Tests").document(testId).collection("Questions")
        FirebaseUtils.mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    questions = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            FirebaseUtils.mQuestion = it.documents.get(snapshot).toObject(Question::class.java)!!
                            questions.add(FirebaseUtils.mQuestion)

                            if (snapshot == it.documents.size - 1)
                                questionsList.value = questions
                        }else{
                            if (snapshot == it.documents.size - 1)
                                questionsList.value = questions
                        }
                    }
                }
            }.addOnFailureListener {
                errorMessage.value = it.message
            }
    }

    fun getQuestionsFromOfDay(testId: String){
        FirebaseUtils.mQuery = FirebaseUtils.mFireStore
            .collection("QuestionsOfDay").document(testId).collection("Questions")
        FirebaseUtils.mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    questions = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            FirebaseUtils.mQuestion = it.documents.get(snapshot).toObject(Question::class.java)!!
                            questions.add(FirebaseUtils.mQuestion)

                            if (snapshot == it.documents.size - 1)
                                questionsList.value = questions
                        }else{
                            if (snapshot == it.documents.size - 1)
                                questionsList.value = questions
                        }
                    }
                }
            }.addOnFailureListener {
                errorMessage.value = it.message
            }
    }

    fun saveTestHistoryData(testHistory: TestHistory, date: String, userId: String){
        FirebaseUtils.mFireStore.collection("Users").document(userId)
            .collection("Tests").document(date).set(testHistory)
    }
}