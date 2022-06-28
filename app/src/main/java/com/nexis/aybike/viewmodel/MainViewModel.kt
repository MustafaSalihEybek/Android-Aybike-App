package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.viewmodel.base.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var questions: ArrayList<Test>

    val questionsOfDayList = MutableLiveData<ArrayList<Test>>()
    val errorMessage = MutableLiveData<String>()
    val checkedDayOfQuestionState = MutableLiveData<Boolean>()

    fun getQuestionsOfDayList(){
        FirebaseUtils.mQuery = FirebaseUtils.mFireStore.collection("QuestionsOfDay")
        FirebaseUtils.mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    questions = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            FirebaseUtils.mTest = it.documents.get(snapshot).toObject(Test::class.java)!!
                            questions.add(FirebaseUtils.mTest)

                            if (snapshot == it.documents.size - 1){
                                questions.shuffle()
                                questionsOfDayList.value = questions
                            }
                        }else{
                            if (snapshot == it.documents.size - 1){
                                questions.shuffle()
                                questionsOfDayList.value = questions
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }
    }

    fun checkDayOfQuestion(userId: String, date: String){
        FirebaseUtils.mDocRef = FirebaseUtils.mFireStore.collection("Users")
            .document(userId).collection("Tests").document(date.replace("/", "-"))
        FirebaseUtils.mDocRef.addSnapshotListener { value, error ->
            if (error != null){
                checkedDayOfQuestionState.value = false
                errorMessage.value = error.localizedMessage
                return@addSnapshotListener
            }

            if (value != null)
                checkedDayOfQuestionState.value = value.exists()
            else
                checkedDayOfQuestionState.value = false
        }
    }
}