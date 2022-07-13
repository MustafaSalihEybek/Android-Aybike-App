package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.aybike.model.*
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.NotifyMessage
import com.nexis.aybike.viewmodel.base.BaseViewModel

class QuestionsViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var questions: ArrayList<Question>

    val questionsList = MutableLiveData<ArrayList<Question>>()
    val errorMessage = MutableLiveData<String>()
    val testViewAmount = MutableLiveData<Int>()
    val testHistoryExists = MutableLiveData<Pair<Boolean, TestHistory?>>()
    val saveTestHistoryState = MutableLiveData<Boolean>()
    val updateDataState = MutableLiveData<Boolean>()
    val userData = MutableLiveData<User>()
    val testSolvedState = MutableLiveData<Boolean>()
    val dialogErrorMessage = MutableLiveData<String>()
    val dialogTestSolvedState = MutableLiveData<Boolean>()

    fun testHistoryExists(categoryName: String, testId: String, userId: String){
        FirebaseUtils.mDocRef = FirebaseUtils.mFireStore.collection("Users")
            .document(userId).collection("$categoryName History").document(testId)
        FirebaseUtils.mDocRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    FirebaseUtils.mTestHistory = it.toObject(TestHistory::class.java)!!
                    testHistoryExists.value = Pair(true, FirebaseUtils.mTestHistory)
                } else
                    testHistoryExists.value = Pair(false, null)
            }.addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }
    }

    fun saveTestHistory(categoryName: String, testId: String, userId: String, testPoint: Float){
        FirebaseUtils.mTestHistory = TestHistory(testId, testPoint)

        FirebaseUtils.mFireStore.collection("Users").document(userId)
            .collection("$categoryName History").document(testId)
            .set(FirebaseUtils.mTestHistory)
            .addOnCompleteListener {
                saveTestHistoryState.value = it.isSuccessful

                if (!it.isSuccessful)
                    errorMessage.value = it.exception?.message
            }
    }

    fun updateUserData(userId: String, data: Map<String, Any>){
        FirebaseUtils.updateUserData(userId, data, object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, updateUserDataOnComplete = {updateState ->
            updateDataState.value = updateState
        })
    }

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

    fun getTestViewAmount(subCategoryId: String, testData: Test){
        FirebaseUtils.getViewAmountOneTime(testData, subCategoryId, object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, getViewAmountOneTimeOnComplete = {viewAmount ->
            testViewAmount.value = viewAmount
        })
    }

    fun getUserData(userId: String){
        FirebaseUtils.getUserDataOneTime(userId, object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, getUserDataOneTimeOnComplete = {user ->
            userData.value = user
        })
    }

    fun saveTestSolution(subCategoryId: String, testId: String, userId: String, testDate1: String, testDate2: String, testDate3: String){
        FirebaseUtils.mTestSolution = TestSolution(testId, userId, testDate1, testDate2, testDate3)

        FirebaseUtils.mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testId).collection("Tests Solved")
            .document(userId).set(FirebaseUtils.mTestSolution)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    errorMessage.value = it.exception?.message

                testSolvedState.value = it.isSuccessful
            }
    }

    fun saveTestSolutionWithDialog(subCategoryId: String, testId: String, userId: String, testDate1: String, testDate2: String, testDate3: String){
        FirebaseUtils.mTestSolution = TestSolution(testId, userId, testDate1, testDate2, testDate3)

        FirebaseUtils.mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testId).collection("Tests Solved")
            .document(userId).set(FirebaseUtils.mTestSolution)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    dialogErrorMessage.value = it.exception?.message

                dialogTestSolvedState.value = it.isSuccessful
            }
    }
}