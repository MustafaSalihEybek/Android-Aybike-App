package com.nexis.aybike.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.nexis.aybike.R
import kotlinx.android.synthetic.main.progress_dialog.*

class ProgressDialog(mContext: Context, val message: String) : Dialog(mContext) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialog)

        progress_dialog_txtMessage.text = message

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}