package com.nexis.aybike.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.view.MainFragmentDirections
import com.nexis.aybike.view.question.QuestionsFragmentDirections
import kotlinx.android.synthetic.main.calculate_point_dialog.*

class CalculatePointDialog(val v: View, val userId: String?, val testData: Test, val subCategoryId: String?, val categoryId: String?, val testDate: String?) : Dialog(v.context), View.OnClickListener {
    private lateinit var navDirections: NavDirections

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculate_point_dialog)

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }

        calculate_point_dialog_btnRestart.setOnClickListener(this)
        calculate_point_dialog_btnBackHome.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.calculate_point_dialog_btnRestart -> restartTest()
                R.id.calculate_point_dialog_btnBackHome -> backToHome()
            }
        }
    }

    private fun restartTest(){
        closeThisDialog()

        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentSelf(subCategoryId, categoryId, testData, userId, testDate)
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun backToHome(){
        closeThisDialog()

        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToMainFragment(userId)
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun closeThisDialog(){
        Singleton.closeCalculatePointDialog()
    }
}