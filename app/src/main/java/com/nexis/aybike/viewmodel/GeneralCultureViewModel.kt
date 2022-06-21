package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.NotifyMessage
import com.nexis.aybike.viewmodel.base.BaseViewModel

class GeneralCultureViewModel(application: Application) : BaseViewModel(application) {
    val subCategoryList = MutableLiveData<ArrayList<SubCategory>>()
    val errorMessage = MutableLiveData<String>()

    fun getSubCategories(categoryName: String){
        FirebaseUtils.getSubCategories(categoryName, object : NotifyMessage {
            override fun onSuccess(message: String) {
                println(message)
            }

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, getSubCategoriesOnComplete = {categoryList: ArrayList<SubCategory>? ->
            subCategoryList.value = categoryList
        })
    }
}