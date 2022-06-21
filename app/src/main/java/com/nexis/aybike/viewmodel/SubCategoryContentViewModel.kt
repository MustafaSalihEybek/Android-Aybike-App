package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.viewmodel.base.BaseViewModel

class SubCategoryContentViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var tests: ArrayList<Test>

    val testList = MutableLiveData<ArrayList<Test>>()
    val errorMessage = MutableLiveData<String>()

    fun getTests(subCategoryId: String){
        FirebaseUtils.mQuery = FirebaseUtils.mFireStore
            .collection("SubCategories").document(subCategoryId).collection("Tests")
        FirebaseUtils.mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    tests = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            FirebaseUtils.mTest = it.documents.get(snapshot).toObject(Test::class.java)!!
                            tests.add(FirebaseUtils.mTest)

                            if (snapshot == it.documents.size - 1)
                                testList.value = tests
                        }else{
                            if (snapshot == it.documents.size - 1)
                                testList.value = tests
                        }
                    }
                }
            }.addOnFailureListener {
                errorMessage.value = it.message
            }
    }
}