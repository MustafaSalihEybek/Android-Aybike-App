package com.nexis.aybike.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.nexis.aybike.R
import com.nexis.aybike.databinding.TestItemBinding
import com.nexis.aybike.model.*
import com.nexis.aybike.util.*
import com.nexis.aybike.view.MainFragmentDirections

class TestsAdapter(var testList: ArrayList<Test>, val subCategory: SubCategory, val userId: String?, val vV: View) : RecyclerView.Adapter<TestsAdapter.TestsHolder>() {
    private lateinit var v: TestItemBinding
    private lateinit var testOnItemClickListener: TestOnItemClickListener
    private lateinit var navDirections: NavDirections
    private lateinit var txtTimeNow: String
    private var aPos: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestsHolder {
        v = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.test_item, parent, false)
        return TestsHolder(v)
    }

    override fun onBindViewHolder(holder: TestsHolder, position: Int) {
        holder.tH.test = testList.get(position)

        FirebaseUtils.getLikedAmount(testList.get(position), subCategory.subCategoryId, object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                message?.let {
                    it.show(vV, it)
                }
            }
        }, getLikedAmountOnComplete = {likedAmount ->
            holder.txtLikedAmount.text = AppUtils.getEditedNumbers(likedAmount)
            FirebaseUtils.updateTestData(testList.get(position).testId, subCategory.subCategoryId, mapOf("testLikeAmount" to likedAmount))
        })

        FirebaseUtils.getViewAmount(testList.get(position), subCategory.subCategoryId, object : NotifyMessage{
            override fun onSuccess(message: String) {}

            override fun onError(message: String?) {
                message?.let {
                    it.show(vV, it)
                }
            }
        }, getViewAmountOnComplete = {viewAmount ->
            holder.txtViewAmount.text = AppUtils.getEditedNumbers(viewAmount)
            FirebaseUtils.updateTestData(testList.get(position).testId, subCategory.subCategoryId, mapOf("testViewAmount" to viewAmount))
        })

        if (userId != null){
            if (!Singleton.userVipStatus)
                isUserSolvedTheTest(subCategory.subCategoryId, testList.get(position).testId, userId, holder)

            FirebaseUtils.checkFavoriteTest(userId, testList.get(position).testId, object : NotifyMessage{
                override fun onSuccess(message: String) {}

                override fun onError(message: String?) {
                    message?.let {
                        it.show(vV, it)
                    }
                }
            }, checkFavoriteTestOnComplete = {checkState ->
                setFavoriteIcons(checkState, holder)
            })

            holder.imgHollowStar.setOnClickListener {
                aPos = holder.adapterPosition

                if (aPos != RecyclerView.NO_POSITION){
                    FirebaseUtils.mTestFavoriteHistory = TestFavoriteHistory(testList.get(aPos).testId, userId)

                    FirebaseUtils.addFavoriteTest(FirebaseUtils.mTestFavoriteHistory, userId, object : NotifyMessage{
                        override fun onSuccess(message: String) {
                            message.show(vV, message)
                            setFavoriteIcons(true, holder)
                        }

                        override fun onError(message: String?) {
                            message?.let {
                                it.show(vV, it)
                            }
                        }
                    })
                }
            }

            holder.imgFilledStar.setOnClickListener {
                aPos = holder.adapterPosition

                if (aPos != RecyclerView.NO_POSITION){
                    FirebaseUtils.removeFavoriteTest(userId, testList.get(aPos).testId, object : NotifyMessage{
                        override fun onSuccess(message: String) {
                            message.show(vV, message)
                            setFavoriteIcons(false, holder)
                        }

                        override fun onError(message: String?) {
                            message?.let {
                                it.show(vV, it)
                            }
                        }
                    })
                }
            }

            FirebaseUtils.checkLikedTest(userId, testList.get(position).testId, object : NotifyMessage{
                override fun onSuccess(message: String) {}

                override fun onError(message: String?) {
                    message?.let {
                        it.show(vV, it)
                    }
                }
            }, checkLikedTestOnComplete = {checkState ->
                setLikedIcons(checkState, holder)
            })

            holder.imgHollowHeart.setOnClickListener {
                aPos = holder.adapterPosition

                if (aPos != RecyclerView.NO_POSITION){
                    FirebaseUtils.mTestLikedHistory = TestLikedHistory(testList.get(aPos).testId, subCategory.subCategoryId)
                    FirebaseUtils.mLikedTestUser = LikedTestUser(userId)
                    FirebaseUtils.addLikeTest(FirebaseUtils.mTestLikedHistory, FirebaseUtils.mLikedTestUser, userId, object : NotifyMessage{
                        override fun onSuccess(message: String) {
                            message.show(vV, message)
                            setLikedIcons(true, holder)
                        }

                        override fun onError(message: String?) {
                            message?.let {
                                it.show(vV, it)
                            }
                        }
                    })
                }
            }

            holder.imgFilledHeart.setOnClickListener {
                aPos = holder.adapterPosition

                if (aPos != RecyclerView.NO_POSITION){
                    FirebaseUtils.removeLikeTest(userId, testList.get(aPos).testId, subCategory.subCategoryId, object : NotifyMessage{
                        override fun onSuccess(message: String) {
                            message.show(vV, message)
                            setLikedIcons(false, holder)
                        }

                        override fun onError(message: String?) {
                            message?.let {
                                it.show(vV, it)
                            }
                        }
                    })
                }
            }
        } else {
            holder.txtViewAmount.text = "0"
            holder.txtLikedAmount.text = "0"
        }

        holder.imgTest.setOnClickListener {
            aPos = holder.adapterPosition

            if (aPos != RecyclerView.NO_POSITION){
                if (holder.itemView.alpha != 0.5f)
                    testOnItemClickListener.onItemClick(subCategory, subCategory.categoryId, testList.get(aPos))
                else
                    goToVipsPage(userId, vV)
            }
        }
    }

    override fun getItemCount() = testList.size

    inner class TestsHolder(var tH: TestItemBinding) : RecyclerView.ViewHolder(tH.root){
        val imgHollowStar: ImageView = tH.root.findViewById(R.id.test_item_imgHollowStar)
        val imgFilledStar: ImageView = tH.root.findViewById(R.id.test_item_imgFilledStar)
        val imgHollowHeart: ImageView = tH.root.findViewById(R.id.test_item_imgHollowHeart)
        val imgFilledHeart: ImageView = tH.root.findViewById(R.id.test_item_imgFilledHeart)
        val imgTest: ImageView = tH.root.findViewById(R.id.test_item_imgTest)
        val txtLikedAmount: TextView = tH.root.findViewById(R.id.test_item_txtLikedAmount)
        val txtViewAmount: TextView = tH.root.findViewById(R.id.test_item_txtViewAmount)
    }

    fun loadData(tests: ArrayList<Test>){
        testList = tests
        notifyDataSetChanged()
    }

    interface TestOnItemClickListener{
        fun onItemClick(subCategory: SubCategory, categoryId: String, testData: Test)
    }

    fun setTestOnItemClickListener(testOnItemClickListener: TestOnItemClickListener){
        this.testOnItemClickListener = testOnItemClickListener
    }

    private fun goToVipsPage(userId: String?, v: View){
        navDirections = MainFragmentDirections.actionMainFragmentToVipsFragment(userId)
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun isUserSolvedTheTest(subCategoryId: String, testId: String, userId: String, holder: TestsHolder?) {
        FirebaseUtils.testIsSolved(subCategoryId, testId,
            userId, testIsSolvedOnComplete = {solvedState, testSolution ->
                if (solvedState){
                    testSolution?.let {
                        txtTimeNow = AppUtils.getFullDateWithString()

                        if (txtTimeNow.equals(it.testDate1) || txtTimeNow.equals(it.testDate2) || txtTimeNow.equals(it.testDate3)){
                            holder?.let {
                                it.itemView.alpha = 0.5f
                            }
                        }
                    }
                }
            })
    }

    private fun setFavoriteIcons(isFavorite: Boolean, holder: TestsHolder){
        if (isFavorite){
            holder.imgFilledStar.visibility = View.VISIBLE
            holder.imgHollowStar.visibility = View.GONE
        } else {
            holder.imgHollowStar.visibility = View.VISIBLE
            holder.imgFilledStar.visibility = View.GONE
        }
    }

    private fun setLikedIcons(isLiked: Boolean, holder: TestsHolder){
        if (isLiked){
            holder.imgFilledHeart.visibility = View.VISIBLE
            holder.imgHollowHeart.visibility = View.GONE
        } else {
            holder.imgHollowHeart.visibility = View.VISIBLE
            holder.imgFilledHeart.visibility = View.GONE
        }
    }
}