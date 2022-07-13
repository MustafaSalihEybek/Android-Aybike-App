package com.nexis.aybike.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavDirections
import com.nexis.aybike.R
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.view.question.QuestionsFragmentDirections

class MainActivity : AppCompatActivity() {
    private lateinit var navDirections: NavDirections

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        if (Singleton.isCurrentMainPage)
            Singleton.showExitTheAppDialog(this)
        else {
            if (Singleton.testCategoryName.equals("EntertainmentCategory")){
                navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToMainFragment(Singleton.userId)
                Singleton.showExitTheTestDialog(Singleton.v, navDirections)
            }
        }
    }
}