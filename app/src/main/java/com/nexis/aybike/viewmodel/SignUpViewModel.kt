package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.nexis.aybike.model.User
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.NotifyMessage
import com.nexis.aybike.viewmodel.base.BaseViewModel

class SignUpViewModel(application: Application) : BaseViewModel(application) {
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val firebaseUser = MutableLiveData<FirebaseUser>()

    fun saveSignUpUserData(userData: User){
        FirebaseUtils.saveSignUpUserData(userData, object : NotifyMessage{
            override fun onSuccess(message: String) {
                successMessage.value = message
            }

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        })
    }

    fun signUpUser(userEmail: String, userPassword: String){
        FirebaseUtils.signUpUser(userEmail, userPassword, object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, signUpUserOnComplete = {fUser ->
            firebaseUser.value = fUser
        })
    }
}