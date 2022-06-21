package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.NotifyMessage
import com.nexis.aybike.viewmodel.base.BaseViewModel

class SignInViewModel(application: Application) : BaseViewModel(application) {
    val userId = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()

    fun signInUser(userEmail: String, userPassword: String){
        FirebaseUtils.signInUser(userEmail, userPassword, object : NotifyMessage{
            override fun onSuccess(message: String) {
                successMessage.value = message
            }

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, signInUserOnComplete = {fUser ->
            userId.value = fUser?.uid
        })
    }

    fun signInUserControl(){
        userId.value = FirebaseUtils.signInUserControl()?.uid
    }
}