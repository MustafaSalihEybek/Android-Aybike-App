package com.nexis.aybike.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.ParseException
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.firebase.Timestamp
import com.nexis.aybike.R
import com.nexis.aybike.adapter.CategoriesViewPagerAdapter
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.aybike_action_bar.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections
    private lateinit var mainViewModel: MainViewModel

    private lateinit var questionsOfDayList: ArrayList<Test>
    private lateinit var randomTest: Test
    private lateinit var categoriesAdapter: CategoriesViewPagerAdapter
    private lateinit var fullDateStr: String
    private var checkedDayOfQuestionState: Boolean = false
    private var userId: String? = null
    private var randomIn: Int = 0
    private var isCurrentPage: Boolean = false

    private fun init(){
        arguments?.let {
            userId = MainFragmentArgs.fromBundle(it).userId

            categoriesAdapter = CategoriesViewPagerAdapter(childFragmentManager)

            categoriesAdapter.addFragment(EntertainmentFragment(userId), "Eğlence")
            categoriesAdapter.addFragment(GeneralCultureFragment(userId), "Genel Kültür")

            main_fragment_viewPager.adapter = categoriesAdapter
            main_fragment_tabLayout.setupWithViewPager(main_fragment_viewPager)

            showActionBarItems()
            aybike_action_bar_imgProfile.setOnClickListener(this)

            mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
            observeLiveData()

            if (userId != null)
                mainViewModel.getQuestionsOfDayList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        isCurrentPage = true
    }

    override fun onPause() {
        super.onPause()
        isCurrentPage = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        mainViewModel.errorMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                it.show(v, it)
            }
        })

        mainViewModel.questionsOfDayList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                questionsOfDayList = it

                randomIn = (0 until questionsOfDayList.size).random()
                randomTest = questionsOfDayList.get(randomIn)

                fullDateStr = getFullDateWithString()
                mainViewModel.checkDayOfQuestion(userId!!, fullDateStr)
            }
        })

        mainViewModel.checkedDayOfQuestionState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                checkedDayOfQuestionState = it
                main_fragment_txtQuestionOfDay.setOnClickListener(this)

                if (it){
                    main_fragment_txtQuestionOfDay.setTextColor(ContextCompat.getColor(v.context, R.color.questionOfDayTxtColor2))
                    main_fragment_txtQuestionOfDay.text = "Günün sorusu çözüldü"
                } else {
                    main_fragment_txtQuestionOfDay.setTextColor(ContextCompat.getColor(v.context, R.color.questionOfDayTxtColor))
                    startTime()
                }
            }
        })
    }

    private fun startTime(){
        Handler(Looper.getMainLooper()).postDelayed({
            if (isCurrentPage){
                calculateTime()

                if (!main_fragment_txtQuestionOfDay.text.equals("Günün Sorusuna Kalan Süre: 0:0:0"))
                    startTime()
            }
        }, 1000)
    }

    private fun calculateTime(){
        val timeNowFromFirebase = Date(Timestamp.now().seconds * 1000)
        val fullTime = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val fullDate = SimpleDateFormat("dd/M/yyyy")
        var fullTimeStr = fullTime.format(timeNowFromFirebase)
        val fullDateStr = fullDate.format(timeNowFromFirebase)
        val fullHours = fullTimeStr.split(" ")[1]
        val twoDigits: String = fullHours.substring(0, 2)
        val reDigits: String = fullHours.substring(2, fullHours.length)
        fullTimeStr = "$fullDateStr ${getEditedHours(twoDigits.toInt())}:$reDigits"

        try {
            val date1: Date = Date(fullTimeStr)
            val date2: Date = Date("$fullDateStr 24:00:00")
            main_fragment_txtQuestionOfDay.text = "Günün Sorusuna Kalan Süre: ${getDifference(date1, date2)}"
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun getEditedHours(twoDigits: Int) : Int {
        if ((twoDigits + 15) > 24)
            return ((twoDigits + 15) - 24)

        return (twoDigits + 15)
    }

    private fun getFullDateWithString() : String {
        val timeNowFromFirebase = Date(Timestamp.now().seconds * 1000)
        val fullDate = SimpleDateFormat("dd/M/yyyy")
        val fullDateStr = fullDate.format(timeNowFromFirebase)

        return fullDateStr
    }

    private fun getDifference(startDate: Date, endDate: Date) : String {
        //milliseconds
        var different = endDate.time - startDate.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli

        return "$elapsedHours:$elapsedMinutes:$elapsedSeconds"
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.aybike_action_bar_imgProfile -> goToSignPage()
                R.id.main_fragment_txtQuestionOfDay -> goToQuestionPage(userId, randomTest)
            }
        }
    }

    private fun goToSignPage(){
        if (userId.isNullOrEmpty())
            navDirections = MainFragmentDirections.actionMainFragmentToSignFragment2(true)
        else
            navDirections = MainFragmentDirections.actionMainFragmentToProfileFragment(userId)

            Navigation.findNavController(v).navigate(navDirections)
    }

    private fun showActionBarItems(){
        aybike_action_bar_imgProfile.visibility = View.VISIBLE
    }

    private fun goToQuestionPage(userId: String?, test: Test){
        if (!checkedDayOfQuestionState) {
            navDirections = MainFragmentDirections.actionMainFragmentToQuestionsFragment(null, null, test, userId, fullDateStr)
            Navigation.findNavController(v).navigate(navDirections)
        }
    }
}