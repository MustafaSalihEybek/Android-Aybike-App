package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.viewmodel.base.BaseViewModel

class SubCategoryContentViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var tests: ArrayList<Test>

    val testList = MutableLiveData<ArrayList<Test>>()
    val errorMessage = MutableLiveData<String>()

    fun getTests(subCategoryId: String, subCategoryName: String, isFilter: Boolean, filterType: String){
        if (!isFilter)
            FirebaseUtils.mQuery = FirebaseUtils.mFireStore
                .collection("SubCategories").document(subCategoryId).collection("Tests")
        else {
            if (filterType.equals("En Eski"))
                FirebaseUtils.mQuery = FirebaseUtils.mFireStore.collection("SubCategories")
                    .document(subCategoryId).collection("Tests").orderBy("testDate", Query.Direction.ASCENDING)
            else if (filterType.equals("En Yeni"))
                FirebaseUtils.mQuery = FirebaseUtils.mFireStore.collection("SubCategories")
                    .document(subCategoryId).collection("Tests").orderBy("testDate", Query.Direction.DESCENDING)
            else if (filterType.equals("En Popüler"))
                FirebaseUtils.mQuery = FirebaseUtils.mFireStore.collection("SubCategories")
                    .document(subCategoryId).collection("Tests").orderBy("testLikeAmount", Query.Direction.DESCENDING)
        }

        FirebaseUtils.mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    tests = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            FirebaseUtils.mTest = it.documents.get(snapshot).toObject(Test::class.java)!!
                            tests.add(FirebaseUtils.mTest)

                            if (snapshot == it.documents.size - 1){
                                if (subCategoryName.lowercase().equals("tümü") && !isFilter)
                                    tests.shuffle()

                                testList.value = tests
                            }
                        }else{
                            if (snapshot == it.documents.size - 1){
                                if (subCategoryName.lowercase().equals("tümü") && !isFilter)
                                    tests.shuffle()

                                testList.value = tests
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                errorMessage.value = it.message
            }
    }
}