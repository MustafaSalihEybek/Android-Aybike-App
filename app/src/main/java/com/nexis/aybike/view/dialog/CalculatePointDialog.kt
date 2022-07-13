package com.nexis.aybike.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.view.question.QuestionsFragmentDirections
import kotlinx.android.synthetic.main.calculate_point_dialog.*

class CalculatePointDialog(val v: View, val totalPoint: Float, val userId: String?, val testData: Test, val subCategoryData: SubCategory?, val categoryId: String?, val testDate: String?, val txtEndMessage: String?) : Dialog(v.context), View.OnClickListener {
    private lateinit var navDirections: NavDirections
    private lateinit var buttonParams: ViewGroup.LayoutParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculate_point_dialog)

        if (txtEndMessage != null)
            calculate_point_dialog_txtTotalPoint.text = "$totalPoint Şöhret Kazandınız\nSonuç: $txtEndMessage"
        else
            calculate_point_dialog_txtTotalPoint.text = "$totalPoint Şöhret Kazandınız"

        if (subCategoryData == null && categoryId == null)
            setHideAndGravityButtons()

        categoryId?.let {
            if (it.equals("GeneralCultureCategory"))
                setHideAndGravityButtons()
        }

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentSelf(subCategoryData, categoryId, testData, userId, testDate)
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

    private fun setHideAndGravityButtons(){
        calculate_point_dialog_linearButtons.gravity = Gravity.CENTER_HORIZONTAL
        calculate_point_dialog_btnRestart.visibility = View.GONE

        buttonParams = calculate_point_dialog_btnBackHome.layoutParams
        buttonParams.width = 80
        calculate_point_dialog_btnBackHome.layoutParams = buttonParams
    }
}