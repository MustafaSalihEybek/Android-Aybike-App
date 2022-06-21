package com.nexis.aybike.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.model.User

object FirebaseUtils {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var fUser: FirebaseUser? = null
    lateinit var mSubCategory: SubCategory
    lateinit var mTest: Test
    lateinit var mQuery: Query
    lateinit var mQuestion: Question
    lateinit var mUser: User

    fun getSubCategories(categoryName: String, notifyMessage: NotifyMessage, getSubCategoriesOnComplete: (categoryList: ArrayList<SubCategory>?) -> Unit){
        var subCategories: ArrayList<SubCategory>?

        mQuery = mFireStore.collection("SubCategories").whereEqualTo("categoryId", categoryName)
        mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    subCategories = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            mSubCategory = it.documents.get(snapshot).toObject(SubCategory::class.java)!!
                            subCategories!!.add(mSubCategory)

                            if (snapshot == it.documents.size - 1){
                                notifyMessage.onSuccess("Test data received successfully")
                                getSubCategoriesOnComplete(subCategories)
                            }
                        }else{
                            if (snapshot == it.documents.size - 1){
                                notifyMessage.onSuccess("Test data received successfully")
                                getSubCategoriesOnComplete(subCategories)
                            }
                        }
                    }
                }else{
                    notifyMessage.onError("Test data not found")
                }
            }.addOnFailureListener {
                notifyMessage.onError(it.message)
            }
    }

    fun signInUser(userEmail: String, userPassword: String, notifyMessage: NotifyMessage, signInUserOnComplete: (fUser: FirebaseUser?) -> Unit){
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    fUser = it.result.user
                    notifyMessage.onSuccess("Başarıyla giriş yaptınız")
                    signInUserOnComplete(fUser)
                } else {
                    notifyMessage.onError(it.exception?.message)
                    signInUserOnComplete(fUser)
                }
            }
    }

    fun signInUserControl() : FirebaseUser? {
        return mAuth.currentUser
    }

    fun signUpUser(userEmail: String, userPassword: String, notifyMessage: NotifyMessage, signUpUserOnComplete: (fUser: FirebaseUser?) -> Unit){
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    fUser = it.result.user
                    signUpUserOnComplete(fUser)
                }else{
                    notifyMessage.onError(it.exception?.message)
                    signUpUserOnComplete(fUser)
                }
            }
    }

    fun saveSignUpUserData(userData: User, notifyMessage: NotifyMessage){
        mFireStore.collection("Users").document(userData.userId)
            .set(userData)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    notifyMessage.onSuccess("Başarıyla kayıt oldunuz")
                else
                    notifyMessage.onError(it.exception?.message)
            }
    }
}