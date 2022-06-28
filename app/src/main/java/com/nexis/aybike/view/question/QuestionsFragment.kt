package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.nexis.aybike.R
import com.nexis.aybike.adapter.QuestionsViewPagerAdapter
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.Test
import com.nexis.aybike.model.TestHistory
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.QuestionsViewModel
import kotlinx.android.synthetic.main.aybike_action_bar.*
import kotlinx.android.synthetic.main.fragment_questions.*

class QuestionsFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections
    private lateinit var questionsViewModel: QuestionsViewModel

    private lateinit var questionsViewPagerAdapter: QuestionsViewPagerAdapter
    private lateinit var questionList: ArrayList<Question>
    private var userId: String? = null
    private lateinit var testData: Test
    private var subCategoryId: String? = null
    private var categoryId: String? = null
    private var testDate: String? = null

    private fun init(){
        arguments?.let {
            userId = QuestionsFragmentArgs.fromBundle(it).userId
            testData = QuestionsFragmentArgs.fromBundle(it).testData
            subCategoryId = QuestionsFragmentArgs.fromBundle(it).subCategoryId
            categoryId = QuestionsFragmentArgs.fromBundle(it).categoryId
            testDate = QuestionsFragmentArgs.fromBundle(it).testDate

            questionsViewPagerAdapter = QuestionsViewPagerAdapter(childFragmentManager)

            questionsViewModel = ViewModelProvider(this).get(QuestionsViewModel::class.java)
            observeLiveData()

            if (subCategoryId != null)
                questionsViewModel.getQuestions(subCategoryId!!, testData.testId)
            else
                questionsViewModel.getQuestionsFromOfDay(testData.testId)

            aybike_action_bar_imgHome.setOnClickListener(this)
            aybike_action_bar_imgProfile.setOnClickListener(this)

            showActionBarItems()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        questionsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        questionsViewModel.questionsList.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.size > 0){
                    questionList = shuffleTheQuestions(it)

                    for (qIn in questionList.indices){
                        questionsViewPagerAdapter.addFragment(getFragment(questionList.get(qIn), (qIn + 1)))
                    }

                    questions_fragment_viewPager.adapter = questionsViewPagerAdapter
                    questions_fragment_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {}

                        override fun onPageSelected(position: Int) {
                            if ((position + 1) == questionList.size)
                                testEndCalculations()
                        }

                        override fun onPageScrollStateChanged(state: Int) {}
                    })

                    if (questionList.size == 1)
                        testEndCalculations()
                }
            }
        })
    }

    private fun testEndCalculations(){
        Singleton.showCalculatePointDialog(v, userId, testData, subCategoryId, categoryId, testDate)

        if (!testDate.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            FirebaseUtils.mTestHistory = TestHistory(testData.testId)
            questionsViewModel.saveTestHistoryData(FirebaseUtils.mTestHistory, testDate!!.replace("/", "-"), userId!!)
        }

        if (userId.isNullOrEmpty())
            Singleton.showSignUpDialog(v)
    }

    private fun shuffleTheQuestions(questions: ArrayList<Question>) : ArrayList<Question> {
        val qSize: Int = questions.size
        val qList: ArrayList<Question> = ArrayList()
        var qIn: Int = 0

        for (q in 0 until qSize){
            qIn = (0 until questions.size).random()
            qList.add(questions.get(qIn))
            questions.removeAt(qIn)
        }

        return qList
    }

    private fun getFragment(question: Question, qIn: Int) : Fragment{
        return when (question.questionType){
            1 -> QuestionType1Fragment(question, qIn)
            2 -> QuestionType2Fragment(question, qIn)
            3 -> QuestionType3Fragment(question, qIn)
            4 -> QuestionType4Fragment(question, qIn)
            else -> QuestionType1Fragment(question, 1)
        }
    }

    private fun showActionBarItems(){
        aybike_action_bar_imgProfile.visibility = View.VISIBLE
        aybike_action_bar_imgHome.visibility = View.VISIBLE
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.aybike_action_bar_imgHome -> goToMainPage()
                R.id.aybike_action_bar_imgProfile -> goToSignInPage()
            }
        }
    }

    private fun goToMainPage(){
        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToMainFragment(userId)
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun goToSignInPage(){
        if (userId.isNullOrEmpty())
            navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToSignFragment(true)
        else
            navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToProfileFragment(userId)

        Navigation.findNavController(v).navigate(navDirections)
    }
}