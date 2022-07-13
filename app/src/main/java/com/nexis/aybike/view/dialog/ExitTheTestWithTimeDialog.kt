package com.nexis.aybike.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.QuestionsViewModel
import kotlinx.android.synthetic.main.exit_the_test_with_time_dialog.*

class ExitTheTestWithTimeDialog(val v: View, val navDirections: NavDirections, val point: Float, val subCategoryId: String, val testId: String, val userId: String, val qV: QuestionsViewModel, val owner: LifecycleOwner) : Dialog(v.context), View.OnClickListener {
    private lateinit var txtFullTime1: String
    private lateinit var txtFullTime2: String
    private lateinit var txtFullTime3: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exit_the_test_with_time_dialog)

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        println("SubCategoryId: $subCategoryId")
        println("testId: $testId")

        exit_the_test_with_time_dialog_txtMessage.text = "Testten çıkarsanız $point şöhret puanı elde edemeyeceksiniz ve 3 gün boyunca bu teste giremeyeceksiniz, testten çıkmak istediğinize emin misiniz?"
        exit_the_test_with_time_dialog_btnNo.setOnClickListener(this)
        exit_the_test_with_time_dialog_btnYes.setOnClickListener(this)

        observeLiveData()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when(it.id){
                R.id.exit_the_test_with_time_dialog_btnNo -> closeThisDialog()
                R.id.exit_the_test_with_time_dialog_btnYes -> btnSaveTestSolution()
            }
        }
    }

    private fun btnSaveTestSolution(){
        txtFullTime1 = AppUtils.getAddedDayTime(0)
        txtFullTime2 = AppUtils.getAddedDayTime(1)
        txtFullTime3 = AppUtils.getAddedDayTime(2)

        Singleton.showProgressDialog(v.context, "Veriler kaydediliyor...")
        qV.saveTestSolutionWithDialog(subCategoryId, testId, userId, txtFullTime1, txtFullTime2, txtFullTime3)
    }

    private fun observeLiveData(){
        qV.dialogErrorMessage.observe(owner, Observer {
            it?.let {
                it.show(v, it)
                Singleton.closeProgressDialog()
            }
        })

        qV.dialogTestSolvedState.observe(owner, Observer {
            it?.let {
                if (it){
                    closeThisDialog()
                    goToMainPage()
                }

                Singleton.closeProgressDialog()
            }
        })
    }

    private fun closeThisDialog(){
        if (this.isShowing)
            this.dismiss()
    }

    private fun goToMainPage(){
        Navigation.findNavController(v).navigate(navDirections)
        Singleton.isCurrentMainPage = true
        closeThisDialog()
    }
}