package com.nexis.aybike.view.question

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

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
    private var testViewAmount: Int = 0
    private var totalPoint: Int = 73

    private lateinit var file: File
    private lateinit var fOut: FileOutputStream
    private lateinit var imageBitmap: Bitmap
    private lateinit var shareMsg: String
    private lateinit var shareIntent: Intent
    private var bmpUri: Uri? = null

    private val signUpMessage: String = "Kazandığınız şöhret puanlarının kaydedilmesi ve çözdüğünüz testi paylaşabilmek için üye olmanız gerekiyor"
    private val shareMessage: String = "Testi arkadaşlarınla paylaşabilmek için üye olmanız gerekiyor"

    private fun init(){
        arguments?.let {
            userId = QuestionsFragmentArgs.fromBundle(it).userId
            testData = QuestionsFragmentArgs.fromBundle(it).testData
            subCategoryId = QuestionsFragmentArgs.fromBundle(it).subCategoryId
            categoryId = QuestionsFragmentArgs.fromBundle(it).categoryId
            testDate = QuestionsFragmentArgs.fromBundle(it).testDate

            Singleton.userId = userId
            Singleton.isCurrentMainPage = false
            Singleton.v = v

            questionsViewPagerAdapter = QuestionsViewPagerAdapter(childFragmentManager)

            questionsViewModel = ViewModelProvider(this).get(QuestionsViewModel::class.java)
            observeLiveData()

            if (subCategoryId != null)
                questionsViewModel.getQuestions(subCategoryId!!, testData.testId)
            else
                questionsViewModel.getQuestionsFromOfDay(testData.testId)

            if (userId != null && subCategoryId != null)
                questionsViewModel.getTestViewAmount(subCategoryId!!, testData)

            aybike_action_bar_imgHome.setOnClickListener(this)
            aybike_action_bar_imgProfile.setOnClickListener(this)
            questions_fragment_btnShare.setOnClickListener(this)
            questions_fragment_btnFinishTest.setOnClickListener(this)

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
        questionsViewModel.testViewAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                testViewAmount = it
                FirebaseUtils.updateTestData(testData.testId, subCategoryId!!, mapOf("testViewAmount" to (testViewAmount + 1)))
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

                    for (qIn in questionList.indices){
                        questionsViewPagerAdapter.addFragment(getFragment(questionList.get(qIn), (qIn + 1), questionList.size))
                    }

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
                }
            }
        })
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

    private fun testEndCalculations(){
        Singleton.showCalculatePointDialog(v, userId, testData, subCategoryId, categoryId, testDate)

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
            1 -> QuestionType1Fragment(question, qIn, qSize)
            2 -> QuestionType2Fragment(question, qIn, qSize)
            3 -> QuestionType3Fragment(question, qIn, qSize)
            4 -> QuestionType4Fragment(question, qIn, qSize)
            else -> QuestionType1Fragment(question, 1, qSize)
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
                R.id.questions_fragment_btnFinishTest -> finishTheTest()
                R.id.questions_fragment_btnShare -> shareTest()
            }
        }
    }

    private fun goToMainPage(){
        navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToMainFragment(userId)
        Singleton.showExitTheTestDialog(v, navDirections)
    }

    private fun goToSignInPage(){
        if (userId.isNullOrEmpty())
            navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToSignFragment(true)
        else
            navDirections = QuestionsFragmentDirections.actionQuestionsFragmentToProfileFragment(userId)

        Singleton.showExitTheTestDialog(v, navDirections)
    }


    private fun shareTest(){
        if (userId != null){
            imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.app_title_img)
            shareMsg = "${resources.getString(R.string.ShareMessage1)} $totalPoint ${resources.getString(R.string.ShareMessage2)} ${resources.getString(R.string.AppUrl)}"

            shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/jpeg"
            bmpUri = saveImage(imageBitmap, v.context)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg)
            startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.ShareTitle)))
        } else
            Singleton.showSignUpDialog(v, shareMessage, true)
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

    private fun finishTheTest(){
        testEndCalculations()
    }
}