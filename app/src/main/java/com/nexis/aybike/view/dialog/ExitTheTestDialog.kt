package com.nexis.aybike.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import kotlinx.android.synthetic.main.exit_the_test_dialog.*

class ExitTheTestDialog(val v: View, val navDirections: NavDirections) : Dialog(v.context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exit_the_test_dialog)

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        exit_the_test_dialog_btnNo.setOnClickListener(this)
        exit_the_test_dialog_btnYes.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.exit_the_test_dialog_btnNo -> closeThisDialog()
                R.id.exit_the_test_dialog_btnYes -> goToPage()
            }
        }
    }

    private fun closeThisDialog(){
        if (this.isShowing)
            this.dismiss()
    }

    private fun goToPage(){
        Navigation.findNavController(v).navigate(navDirections)
        closeThisDialog()
    }
}