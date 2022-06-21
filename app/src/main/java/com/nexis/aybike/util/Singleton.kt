package com.nexis.aybike.util

import android.content.Context
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.nexis.aybike.model.Test
import com.nexis.aybike.view.dialog.CalculatePointDialog
import com.nexis.aybike.view.dialog.SignUpDialog

class Singleton {
    companion object{
        var mViewPager: ViewPager? = null
        var calculatePointDialog: CalculatePointDialog? = null
        var signUpDialog: SignUpDialog? = null

        fun showPageFromViewPager(pIn: Int){
            mViewPager?.let {
                it.currentItem = pIn
            }
        }

        fun showCalculatePointDialog(v: View, userId: String?, testData: Test, subCategoryId: String, categoryId: String){
            calculatePointDialog = CalculatePointDialog(v, userId, testData, subCategoryId, categoryId)
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
    }
}