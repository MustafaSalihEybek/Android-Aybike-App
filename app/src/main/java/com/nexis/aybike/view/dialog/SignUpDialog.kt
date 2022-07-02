package com.nexis.aybike.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.view.question.QuestionsFragmentDirections
import kotlinx.android.synthetic.main.sign_up_dialog.*

class SignUpDialog(val v: View, val message: String) : Dialog(v.context), View.OnClickListener {
    private lateinit var navDirections: NavDirections

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_dialog)

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        sign_up_dialog_txtMessage.text = message
        sign_up_dialog_btnClose.setOnClickListener(this)
        sign_up_dialog_btnSignIn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.sign_up_dialog_btnClose -> closeThisDialog()
                R.id.sign_up_dialog_btnSignIn -> goToSignInPage()
            }
        }
    }

    private fun goToSignInPage(){
        closeThisDialog()
        Singleton.closeCalculatePointDialog()

        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToSignFragment(true)
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun closeThisDialog(){
        Singleton.closeSignUpDialog()
    }
}