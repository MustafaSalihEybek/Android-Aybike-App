package com.nexis.aybike.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nexis.aybike.model.*

object FirebaseUtils {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var fUser: FirebaseUser? = null
    lateinit var mSubCategory: SubCategory
    lateinit var mTest: Test
    lateinit var mQuery: Query
    lateinit var mQuestion: Question
    lateinit var mUser: User
    lateinit var mDocRef: DocumentReference
    lateinit var mTestHistory: TestHistory
    lateinit var mTestFavoriteHistory: TestFavoriteHistory
    lateinit var mTestLikedHistory: TestLikedHistory
    lateinit var mLikedTestUser: LikedTestUser
    lateinit var mTestSolution: TestSolution
    lateinit var mShopSub: ShopSub
    lateinit var mShopSubHistory: ShopSubHistory

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

    fun checkFavoriteTest(userId: String, testId: String, notifyMessage: NotifyMessage, checkFavoriteTestOnComplete: (checkState: Boolean) -> Unit){
        mDocRef = mFireStore.collection("Users").document(userId)
            .collection("Tests Favorite Histories").document(testId)
        mDocRef.get()
            .addOnSuccessListener {
                checkFavoriteTestOnComplete(it.exists())
            }.addOnFailureListener {
                notifyMessage.onError(it.localizedMessage)
                checkFavoriteTestOnComplete(false)
            }
    }

    fun addFavoriteTest(testFavoriteHistory: TestFavoriteHistory, userId: String, notifyMessage: NotifyMessage){
        mFireStore.collection("Users").document(userId)
            .collection("Tests Favorite Histories").document(testFavoriteHistory.testId)
            .set(testFavoriteHistory)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    notifyMessage.onSuccess("Test favorilere başarıyla eklendi")
                else
                    notifyMessage.onError(it.exception?.message)
            }
    }

    fun removeFavoriteTest(userId: String, testId: String, notifyMessage: NotifyMessage){
        mFireStore.collection("Users").document(userId)
            .collection("Tests Favorite Histories").document(testId)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    notifyMessage.onSuccess("Test favorilerden başarıyla kaldırıldı")
                else
                    notifyMessage.onError(it.exception?.message)
            }
    }

    fun checkLikedTest(userId: String, testId: String, notifyMessage: NotifyMessage, checkLikedTestOnComplete: (checkState: Boolean) -> Unit){
        mDocRef = mFireStore.collection("Users").document(userId)
            .collection("Tests Like Histories").document(testId)
        mDocRef.get()
            .addOnSuccessListener {
                checkLikedTestOnComplete(it.exists())
            }.addOnFailureListener {
                notifyMessage.onError(it.localizedMessage)
                checkLikedTestOnComplete(false)
            }
    }

    fun addLikeTest(testLikedHistory: TestLikedHistory, testLikedTestUser: LikedTestUser, userId: String, notifyMessage: NotifyMessage){
        mFireStore.collection("Users").document(userId)
            .collection("Tests Like Histories").document(testLikedHistory.testId)
            .set(testLikedHistory)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    mFireStore.collection("SubCategories").document(testLikedHistory.testSubCategoryId)
                        .collection("Tests").document(testLikedHistory.testId).collection("Users Like").document(userId)
                        .set(testLikedTestUser)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                notifyMessage.onSuccess("Test başarıyla beğenildi")
                            else
                                notifyMessage.onError(it.exception?.message)
                        }
                }
                else
                    notifyMessage.onError(it.exception?.message)
            }
    }

    fun removeLikeTest(userId: String, testId: String, subCategoryId: String, notifyMessage: NotifyMessage){
        mFireStore.collection("SubCategories").document(subCategoryId).collection("Tests")
            .document(testId).collection("Users Like").document(userId)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    mFireStore.collection("Users").document(userId)
                        .collection("Tests Like Histories").document(testId)
                        .delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                notifyMessage.onSuccess("Test beğenilerden başarıyla kaldırıldı")
                            else
                                notifyMessage.onError(it.exception?.message)
                        }
                } else
                    notifyMessage.onError(it.exception?.message)
            }
    }

    fun getLikedAmount(testData: Test, subCategoryId: String, notifyMessage: NotifyMessage, getLikedAmountOnComplete: (likedAmount: Int) -> Unit){
        mQuery = mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testData.testId).collection("Users Like")
        mQuery.addSnapshotListener { value, error ->
            if (error != null){
                notifyMessage.onError(error.localizedMessage)
                getLikedAmountOnComplete(0)
                return@addSnapshotListener
            }

            if (value != null)
                getLikedAmountOnComplete(value.documents.size)
            else
                getLikedAmountOnComplete(0)
        }
    }

    fun getViewAmount(testData: Test, subCategoryId: String, notifyMessage: NotifyMessage, getViewAmountOnComplete: (viewAmount: Int) -> Unit){
        mDocRef = mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testData.testId)
        mDocRef.addSnapshotListener { value, error ->
            if (error != null){
                notifyMessage.onError(error.localizedMessage)
                getViewAmountOnComplete(0)
                return@addSnapshotListener
            }

            if (value != null){
                mTest = value.toObject(Test::class.java)!!
                getViewAmountOnComplete(mTest.testViewAmount)
            } else
                getViewAmountOnComplete(0)
        }
    }

    fun getViewAmountOneTime(testData: Test, subCategoryId: String, notifyMessage: NotifyMessage, getViewAmountOneTimeOnComplete: (viewAmount: Int) -> Unit){
        mDocRef = mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testData.testId)
        mDocRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    mTest = it.toObject(Test::class.java)!!
                    getViewAmountOneTimeOnComplete(mTest.testViewAmount)
                } else
                    getViewAmountOneTimeOnComplete(0)
            }.addOnFailureListener {
                notifyMessage.onError(it.localizedMessage)
                getViewAmountOneTimeOnComplete(0)
            }
    }

    fun updateTestData(testId: String, subCategoryId: String, data: Map<String, Any>){
        mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testId).update(data)
    }

    fun updateUserData(userId: String, data: Map<String, Any>, notifyMessage: NotifyMessage, updateUserDataOnComplete: (updateState: Boolean) -> Unit){
        mFireStore.collection("Users").document(userId)
            .update(data)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    updateUserDataOnComplete(true)
                else {
                    notifyMessage.onError(it.exception?.message)
                    updateUserDataOnComplete(false)
                }
            }
    }

    fun getUserDataOneTime(userId: String, notifyMessage: NotifyMessage, getUserDataOneTimeOnComplete: (user: User) -> Unit){
        mDocRef = mFireStore.collection("Users").document(userId)
        mDocRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    mUser = it.toObject(User::class.java)!!
                    getUserDataOneTimeOnComplete(mUser)
                }
            }.addOnFailureListener {
                notifyMessage.onError(it.message)
            }
    }

    fun testIsSolved(subCategoryId: String, testId: String, userId: String, testIsSolvedOnComplete: (solvedState: Boolean, testSolution: TestSolution?) -> Unit){
        mDocRef = mFireStore.collection("SubCategories").document(subCategoryId)
            .collection("Tests").document(testId).collection("Tests Solved").document(userId)
        mDocRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    mTestSolution = it.toObject(TestSolution::class.java)!!
                    testIsSolvedOnComplete(true, mTestSolution)
                } else
                    testIsSolvedOnComplete(false, null)
            }.addOnFailureListener {
                testIsSolvedOnComplete(false, null)
            }
    }

    fun getShopSubList(notifyMessage: NotifyMessage, getShopSubListOnComplete: (shopTuple: Pair<ArrayList<ShopSub>, ArrayList<String>>) -> Unit){
        var shopList: ArrayList<ShopSub>
        var skuList: ArrayList<String>

        mQuery = mFireStore.collection("ShopItemSubs").orderBy("shopItemNumber", Query.Direction.ASCENDING)
        mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    shopList = ArrayList()
                    skuList = ArrayList()

                    for (snapshot in it.documents.indices){
                        if (it.documents.get(snapshot).exists()){
                            mShopSub = it.documents.get(snapshot).toObject(ShopSub::class.java)!!

                            shopList.add(mShopSub)
                            skuList.add(mShopSub.shopItemSkuId)

                            if (snapshot == it.documents.size - 1)
                                getShopSubListOnComplete(Pair(shopList, skuList))
                        } else {
                            if (snapshot == it.documents.size - 1)
                                getShopSubListOnComplete(Pair(shopList, skuList))
                        }
                    }
                }
            }.addOnFailureListener {
                notifyMessage.onError(it.message)
            }
    }
}