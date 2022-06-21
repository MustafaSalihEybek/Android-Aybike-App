package com.nexis.aybike.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nexis.aybike.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {

    }
}