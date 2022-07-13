package com.nexis.aybike.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.nexis.aybike.model.ShopSub
import com.nexis.aybike.model.ShopSubHistory
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.NotifyMessage
import com.nexis.aybike.viewmodel.base.BaseViewModel

class VipsViewModel(application: Application) : BaseViewModel(application) {
    val shopTuple = MutableLiveData<Pair<ArrayList<ShopSub>, ArrayList<String>>>()
    val errorMessage = MutableLiveData<String>()
    val saveHistoryState = MutableLiveData<Boolean>()
    val successMessage = MutableLiveData<String>()

    fun getShopSubs(){
        FirebaseUtils.getShopSubList(object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                errorMessage.value = message
            }
        }, getShopSubListOnComplete = {shopTuple ->
            this.shopTuple.value = shopTuple
        })
    }

    fun saveBuyShopSubHistory(shopSub: ShopSub, userId: String, purchaseId: String){
        FirebaseUtils.mShopSubHistory = ShopSubHistory(userId, shopSub.documentId, purchaseId, FieldValue.serverTimestamp())

        FirebaseUtils.mFireStore.collection("ShopItemSubs").document(shopSub.documentId)
            .collection("Purchase Histories").document(purchaseId).set(FirebaseUtils.mShopSubHistory)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    errorMessage.value = it.exception?.message

                saveHistoryState.value = it.isSuccessful
            }
    }

    fun saveUserData(userId: String, data: Map<String, Any>){
        FirebaseUtils.mFireStore.collection("Users").document(userId)
            .update(data)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    successMessage.value = "Satın alma işlemi başarıyla gerçekleşti"
                else
                    errorMessage.value = it.exception?.message
            }
    }
}