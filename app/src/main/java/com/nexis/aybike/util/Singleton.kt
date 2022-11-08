package com.nexis.aybike.util

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.viewpager.widget.ViewPager
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.view.dialog.*
import com.nexis.aybike.viewmodel.QuestionsViewModel

class Singleton {
    companion object{
        var mViewPager: ViewPager? = null
        var mTestViewPager: ViewPager? = null
        var isCurrentMainPage: Boolean = false
        var userId: String? = null
        var dataIsSaved: Boolean = false
        var userVipStatus: Boolean = false
        var testCategoryName: String = ""
        var choiceAmountList: Array<Int> = arrayOf(0, 0, 0, 0)
        lateinit var v: View
        lateinit var questionSolvedList: Array<Boolean>

        private var calculatePointDialog: CalculatePointDialog? = null
        private var signUpDialog: SignUpDialog? = null
        private var progressDialog: ProgressDialog? = null
        private lateinit var exitTheAppDialog: ExitTheAppDialog
        private lateinit var exitTheTestDialog: ExitTheTestDialog
        private lateinit var exitTheTestWithTimeDialog: ExitTheTestWithTimeDialog

        fun showPageFromViewPager(pIn: Int){
            mViewPager?.let {
                it.currentItem = pIn
            }
        }

        fun showCalculatePointDialog(v: View, totalPoint: Float, userId: String?, testData: Test, subCategoryData: SubCategory?, categoryId: String?, testDate: String?, txtEndMessage: String?){
            calculatePointDialog = CalculatePointDialog(v, totalPoint, userId, testData, subCategoryData, categoryId, testDate, txtEndMessage)
            calculatePointDialog!!.show()
        }

        fun closeCalculatePointDialog(){
            calculatePointDialog?.let {
                if (it.isShowing)
                    it.dismiss()
            }
        }

        fun showSignUpDialog(v: View, message: String, isCancel: Boolean, fromMain: Boolean){
            signUpDialog = SignUpDialog(v, message, fromMain)
            signUpDialog!!.setCancelable(isCancel)
            signUpDialog!!.show()
        }

        fun closeSignUpDialog(){
            signUpDialog?.let {
                if (it.isShowing)
                    it.dismiss()
            }
        }

        fun setNextQuestionPage(aSize: Int){
            mTestViewPager?.let {
                if (it.currentItem < (aSize - 1))
                    it.currentItem = (it.currentItem + 1)
            }
        }

        fun showExitTheAppDialog(mContext: Context){
            exitTheAppDialog = ExitTheAppDialog(mContext)
            exitTheAppDialog.setCancelable(false)
            exitTheAppDialog.show()
        }

        fun showExitTheTestDialog(v: View, navDirections: NavDirections){
            exitTheTestDialog = ExitTheTestDialog(v, navDirections)
            exitTheTestDialog.setCancelable(false)
            exitTheTestDialog.show()
        }

        fun showExitTheTestWithTimeDialog(v: View, navDirections: NavDirections, point: Float, subCategoryId: String, testId: String, userId: String, qV: QuestionsViewModel, owner: LifecycleOwner){
            exitTheTestWithTimeDialog = ExitTheTestWithTimeDialog(v, navDirections, point, subCategoryId, testId, userId, qV, owner)
            exitTheTestWithTimeDialog.setCancelable(false)
            exitTheTestWithTimeDialog.show()
        }

        fun showProgressDialog(mContext: Context, message: String){
            progressDialog = ProgressDialog(mContext, message)
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        fun closeProgressDialog(){
            progressDialog?.let {
                if (it.isShowing)
                    it.dismiss()
            }
        }
    }
}