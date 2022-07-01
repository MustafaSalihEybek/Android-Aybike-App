package com.nexis.aybike.util

import android.content.Context
import android.view.View
import androidx.navigation.NavDirections
import androidx.viewpager.widget.ViewPager
import com.nexis.aybike.model.Test
import com.nexis.aybike.view.dialog.CalculatePointDialog
import com.nexis.aybike.view.dialog.ExitTheAppDialog
import com.nexis.aybike.view.dialog.ExitTheTestDialog
import com.nexis.aybike.view.dialog.SignUpDialog

class Singleton {
    companion object{
        var mViewPager: ViewPager? = null
        var mTestViewPager: ViewPager? = null
        var isCurrentMainPage: Boolean = false
        var userId: String? = null
        lateinit var v: View

        private var calculatePointDialog: CalculatePointDialog? = null
        private var signUpDialog: SignUpDialog? = null
        private lateinit var exitTheAppDialog: ExitTheAppDialog
        private lateinit var exitTheTestDialog: ExitTheTestDialog

        fun showPageFromViewPager(pIn: Int){
            mViewPager?.let {
                it.currentItem = pIn
            }
        }

        fun showCalculatePointDialog(v: View, userId: String?, testData: Test, subCategoryId: String?, categoryId: String?, testDate: String?){
            calculatePointDialog = CalculatePointDialog(v, userId, testData, subCategoryId, categoryId, testDate)
            calculatePointDialog!!.setCancelable(false)
            calculatePointDialog!!.show()
        }

        fun closeCalculatePointDialog(){
            calculatePointDialog?.let {
                if (it.isShowing)
                    it.dismiss()
            }
        }

        fun showSignUpDialog(v: View){
            signUpDialog = SignUpDialog(v)
            signUpDialog!!.setCancelable(false)
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
    }
}