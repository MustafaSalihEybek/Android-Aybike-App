package com.nexis.aybike.view.question

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.viewpager.widget.ViewPager
import com.nexis.aybike.R
import com.nexis.aybike.adapter.QuestionsViewPagerAdapter
import com.nexis.aybike.model.*
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.QuestionsViewModel
import kotlinx.android.synthetic.main.aybike_action_bar.*
import kotlinx.android.synthetic.main.fragment_questions.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class QuestionsFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections
    private lateinit var questionsViewModel: QuestionsViewModel

    private lateinit var questionsViewPagerAdapter: QuestionsViewPagerAdapter
    private lateinit var questionList: ArrayList<Question>
    private lateinit var questionSolvedList: Array<Boolean>
    private var userId: String? = null
    private lateinit var testData: Test
    private var subCategoryData: SubCategory? = null
    private lateinit var subCategoryId: String
    private var categoryId: String? = null
    private var testDate: String? = null
    private var testViewAmount: Int = 0

    private lateinit var file: File
    private lateinit var fOut: FileOutputStream
    private lateinit var imageBitmap: Bitmap
    private lateinit var shareMsg: String
    private lateinit var shareIntent: Intent
    private var bmpUri: Uri? = null

    private val signUpMessage: String = "Kazandığınız şöhret puanlarının kaydedilmesi ve çözdüğünüz testi paylaşabilmek için üye olmanız gerekiyor"
    private val shareMessage: String = "Testi arkadaşlarınla paylaşabilmek için üye olmanız gerekiyor"

    private lateinit var testHistory: TestHistory
    private var testHistoryExists: Boolean = false
    private var dataIsSaved: Boolean = false
    private var userData: User? = null
    private var totalPoint: Float = 0F

    private lateinit var txtFullTime1: String
    private lateinit var txtFullTime2: String
    private lateinit var txtFullTime3: String

    private lateinit var questionFragmentList: ArrayList<Fragment>
    private lateinit var questionFragment: Fragment

    private var txtEndMessage: String? = null

    private fun init(){
        arguments?.let {
            userId = QuestionsFragmentArgs.fromBundle(it).userId
            testData = QuestionsFragmentArgs.fromBundle(it).testData
            subCategoryData = QuestionsFragmentArgs.fromBundle(it).subCategoryData
            categoryId = QuestionsFragmentArgs.fromBundle(it).categoryId
            testDate = QuestionsFragmentArgs.fromBundle(it).testDate

            Singleton.dataIsSaved = dataIsSaved
            Singleton.userId = userId
            Singleton.isCurrentMainPage = false
            Singleton.v = v

            questionsViewPagerAdapter = QuestionsViewPagerAdapter(childFragmentManager)

            questionsViewModel = ViewModelProvider(this).get(QuestionsViewModel::class.java)
            observeLiveData()

            if (subCategoryData != null){
                Singleton.testCategoryName = subCategoryData!!.categoryId

                subCategoryId = subCategoryData!!.subCategoryId
                questionsViewModel.getQuestions(subCategoryId, testData.testId)
                questionsViewModel.getTestViewAmount(subCategoryId, testData)
            }else
                questionsViewModel.getQuestionsFromOfDay(testData.testId)

            if (userId != null)
                questionsViewModel.getUserData(userId!!)

            questions_fragment_btnFinishTest.setOnClickListener(this)
            questions_fragment_btnShare.setOnClickListener(this)

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
        questionsViewModel.testHistoryExists.observe(viewLifecycleOwner, Observer {
            it?.let {
                testHistoryExists = it.first

                if (!testHistoryExists){
                    totalPoint = getTotalPoint(testHistoryExists, questionList, subCategoryData?.categoryId)
                    questionsViewModel.saveTestHistory(subCategoryData!!.categoryId, testData.testId, userId!!, totalPoint)
                } else {
                    testHistory = it.second!!
                    totalPoint = getTotalPoint(testHistoryExists, questionList, subCategoryData?.categoryId)

                    if (subCategoryData!!.categoryId.equals("GeneralCultureCategory")){
                        setAnswerPropertiesFromGeneralCulture()
                        questionsViewModel.saveTestHistory(subCategoryData!!.categoryId, testData.testId, userId!!, totalPoint)
                    } else
                        questionsViewModel.updateUserData(userId!!, mapOf("userPoint" to (userData!!.userPoint + totalPoint)))
                }
            }
        })

        questionsViewModel.saveTestHistoryState.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    if (testHistoryExists)
                        totalPoint -= testHistory.testPoint

                    questionsViewModel.updateUserData(userId!!, mapOf("userPoint" to (userData!!.userPoint + totalPoint)))
                }
            }
        })

        questionsViewModel.updateDataState.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it)
                    dataIsSaved = true

                if (subCategoryData != null){
                    if (subCategoryData!!.categoryId.equals("GeneralCultureCategory")){
                        txtFullTime1 = AppUtils.getAddedDayTime(0)
                        txtFullTime2 = AppUtils.getAddedDayTime(1)
                        txtFullTime3 = AppUtils.getAddedDayTime(2)

                        questionsViewModel.saveTestSolution(subCategoryId, testData.testId, userId!!, txtFullTime1, txtFullTime2, txtFullTime3)
                    } else {
                        Singleton.dataIsSaved = dataIsSaved
                        Singleton.closeProgressDialog()
                        testEndCalculations(totalPoint)
                    }
                } else {
                    Singleton.dataIsSaved = dataIsSaved
                    Singleton.closeProgressDialog()
                    testEndCalculations(totalPoint)
                }
            }
        })

        questionsViewModel.testSolvedState.observe(viewLifecycleOwner, Observer {
            it?.let {
                Singleton.dataIsSaved = dataIsSaved
                Singleton.closeProgressDialog()
                testEndCalculations(totalPoint)
            }
        })

        questionsViewModel.userData.observe(viewLifecycleOwner, Observer {
            it?.let {
                userData = it
            }
        })

        questionsViewModel.testViewAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                testViewAmount = it
                FirebaseUtils.updateTestData(testData.testId, subCategoryId, mapOf("testViewAmount" to (testViewAmount + 1)))
            }
        })

        questionsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        questionsViewModel.questionsList.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.size > 0){
                    questionList = shuffleTheQuestions(it)
                    questionSolvedList = arrayOf()
                    questionFragmentList = ArrayList()

                    for (qIn in questionList.indices){
                        questionSolvedList = appendSolved(questionSolvedList, false)
                        questionFragment = getFragment(questionList.get(qIn), (qIn + 1), questionList.size)

                        if (questionList.get(qIn).questionType == 1)
                            questionFragment as QuestionType1Fragment

                        questionFragmentList.add(questionFragment)
                        questionsViewPagerAdapter.addFragment(questionFragment)
                    }

                    Singleton.correctAndWrongAmountTuple = Pair(0, 0)
                    Singleton.questionSolvedList = questionSolvedList

                    questions_fragment_viewPager.adapter = questionsViewPagerAdapter
                    Singleton.mTestViewPager = questions_fragment_viewPager
                    questions_fragment_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                            setVisibilityButtons(((position + 1) != questionList.size))
                        }

                        override fun onPageSelected(position: Int) {}

                        override fun onPageScrollStateChanged(state: Int) {}
                    })

                    if (questionList.size == 1)
                        setVisibilityButtons(true)

                    aybike_action_bar_imgHome.setOnClickListener(this)
                    aybike_action_bar_imgProfile.setOnClickListener(this)
                }
            }
        })
    }

    private fun setAnswerPropertiesFromGeneralCulture(){
        for (qIn in questionList.indices){
            if (questionList.get(qIn).questionType == 1)
                (questionFragmentList.get(qIn) as QuestionType1Fragment).checkTheAnswer()
            else if (questionList.get(qIn).questionType == 2)
                (questionFragmentList.get(qIn) as QuestionType2Fragment).checkTheAnswer()
            else if (questionList.get(qIn).questionType == 3)
                (questionFragmentList.get(qIn) as QuestionType3Fragment).checkTheAnswer()
            else
                (questionFragmentList.get(qIn) as QuestionType4Fragment).checkTheAnswer()
        }
    }

    private fun appendSolved(solvedList: Array<Boolean>, solved: Boolean) : Array<Boolean> {
        val list: MutableList<Boolean> = solvedList.toMutableList()
        list.add(solved)
        return list.toTypedArray()
    }

    private fun setVisibilityButtons(setHide: Boolean){
        if (!setHide){
            questions_fragment_btnFinishTest.visibility = View.VISIBLE
            questions_fragment_btnShare.visibility = View.VISIBLE
        } else {
            questions_fragment_btnFinishTest.visibility = View.GONE
            questions_fragment_btnShare.visibility = View.GONE
        }
    }

    private fun testEndCalculations(totalPoint: Float){
        subCategoryData?.let {
            if (it.categoryId.equals("EntertainmentCategory")){
                if (Singleton.correctAndWrongAmountTuple.first > Singleton.correctAndWrongAmountTuple.second)
                    txtEndMessage = testData.testEndMessages.get(0)
                else if (Singleton.correctAndWrongAmountTuple.second > Singleton.correctAndWrongAmountTuple.first)
                    txtEndMessage = testData.testEndMessages.get(1)
                else
                    txtEndMessage = testData.testEndMessages.get(0)
            }
        }

        Singleton.showCalculatePointDialog(v, totalPoint, userId, testData, subCategoryData, categoryId, testDate, txtEndMessage)

        if (!testDate.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            FirebaseUtils.mTestHistory = TestHistory(testData.testId)
            questionsViewModel.saveTestHistoryData(FirebaseUtils.mTestHistory, testDate!!.replace("/", "-"), userId!!)
        }

        if (userId.isNullOrEmpty())
            Singleton.showSignUpDialog(v, signUpMessage, false)
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

    private fun getFragment(question: Question, qIn: Int, qSize: Int) : Fragment{
        return when (question.questionType){
            1 -> QuestionType1Fragment(question, subCategoryData, qIn, qSize)
            2 -> QuestionType2Fragment(question, subCategoryData, qIn, qSize)
            3 -> QuestionType3Fragment(question, subCategoryData, qIn, qSize)
            4 -> QuestionType4Fragment(question, subCategoryData, qIn, qSize)
            else -> QuestionType1Fragment(question, subCategoryData, 1, qSize)
        }
    }

    private fun showActionBarItems(){
        aybike_action_bar_imgProfile.visibility = View.VISIBLE
        aybike_action_bar_imgHome.visibility = View.VISIBLE
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.aybike_action_bar_imgHome -> goToMainPage(subCategoryData, userId, testData.testId)
                R.id.aybike_action_bar_imgProfile -> goToSignInPage(subCategoryData, userId, testData.testId)
                R.id.questions_fragment_btnFinishTest -> finishTheTest(subCategoryData?.categoryId, testData.testId, userId, testHistoryExists, questionList, questionSolvedList)
                R.id.questions_fragment_btnShare -> shareTest(userData)
            }
        }
    }

    private fun goToMainPage(subCategory: SubCategory?, userId: String?, testId: String){
        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToMainFragment(userId)
        showExitTheTestWithTimeDialog(subCategory, userId, testId, navDirections)
    }

    private fun goToSignInPage(subCategory: SubCategory?, userId: String?, testId: String){
        if (userId.isNullOrEmpty())
            navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToSignFragment(true)
        else
            navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToProfileFragment(userId)

        showExitTheTestWithTimeDialog(subCategory, userId, testId, navDirections)
    }

    private fun showExitTheTestWithTimeDialog(subCategory: SubCategory?, userId: String?, testId: String, navDirections: NavDirections){
        if (subCategory != null && userId != null){
            if (subCategoryData!!.categoryId.equals("GeneralCultureCategory")){
                totalPoint = getTotalPoint(testHistoryExists, questionList, subCategory.categoryId)
                Singleton.showExitTheTestWithTimeDialog(v, navDirections, totalPoint, subCategory.subCategoryId, testId, userId, questionsViewModel, viewLifecycleOwner)
            } else
                Singleton.showExitTheTestDialog(v, navDirections)
        } else
            Singleton.showExitTheTestDialog(v, navDirections)
    }

    private fun shareTest(userData: User?){
        if (userId != null){
            totalPoint = getTotalPoint(testHistoryExists, questionList, subCategoryData?.categoryId)
            imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.app_title_img)
            shareMsg = "${resources.getString(R.string.ShareMessage1)} $totalPoint ${resources.getString(R.string.ShareMessage2)} ${resources.getString(R.string.AppUrl)}"

            shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/jpeg"
            bmpUri = saveImage(imageBitmap, v.context)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg)
            startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.ShareTitle)))

            updateUserPointForShare(userData!!)
        } else
            Singleton.showSignUpDialog(v, shareMessage, true)
    }

    private fun updateUserPointForShare(userData: User){
        FirebaseUtils.mFireStore.collection("Users").document(userData.userId)
            .update(mapOf("userPoint" to (userData.userPoint + 20)))
    }

    private fun saveImage(image: Bitmap, context: Context) : Uri? {
        file = File(context.cacheDir, "images")
        var uri: Uri? = null

        try {
            file.mkdirs()
            val fFile = File(file, "shared_images.jpg")

            fOut = FileOutputStream(fFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()

            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.applicationContext), "com.nexis.aybike.provider", fFile)
        } catch (e: IOException){
            println(e.message)
        }

        return uri
    }

    private fun finishTheTest(categoryName: String?, testId: String, userId: String?, testHistoryExists: Boolean, questionList: ArrayList<Question>, questionSolvedList: Array<Boolean>){
        if (questionAllSolved(questionSolvedList)) {
            if (userId != null && categoryName != null && userData != null){
                if (!dataIsSaved){
                    Singleton.showProgressDialog(v.context, "Veriler kaydediliyor...")
                    questionsViewModel.testHistoryExists(categoryName, testId, userId)
                }
                else
                    testEndCalculations((getTotalPoint(testHistoryExists, questionList, categoryName)))
            } else {
                if (userId != null){
                    Singleton.showProgressDialog(v.context, "Veriler kaydediliyor...")

                    totalPoint = getTotalPoint(testHistoryExists, questionList, subCategoryData?.categoryId)
                    questionsViewModel.updateUserData(userId, mapOf("userPoint" to (userData!!.userPoint + totalPoint)))
                } else {
                    setAnswerPropertiesFromGeneralCulture()
                    Singleton.dataIsSaved = dataIsSaved
                    Singleton.closeProgressDialog()
                    testEndCalculations(totalPoint)
                }
            }
        } else
            "message".show(v, "Lütfen tüm soruları çözdüğünüzden emin olun")
    }

    private fun questionAllSolved(questionSolvedList: Array<Boolean>) : Boolean {
        var allState: Boolean = false

        questionSolvedList.map { state ->
            allState = state

            if (!state)
                return allState
        }

        return allState
    }

    private fun getTotalPoint(testHistoryExists: Boolean, questionList: ArrayList<Question>, categoryName: String?) : Float {
        var totalPoint: Float = 0f

        if (categoryName != null){
            if (categoryName.equals("EntertainmentCategory")){
                for (question in questionList){
                    if (!testHistoryExists)
                        totalPoint += (question.questionPoint.toFloat())
                    else
                        totalPoint += ((question.questionPoint.toFloat()) / 4f)
                }
            }else
                totalPoint = (questionList.size * 3).toFloat()
        }else
            totalPoint = questionList.get(0).questionPoint.toFloat()

        return totalPoint
    }
}