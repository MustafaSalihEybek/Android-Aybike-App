package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType2Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.downloadImageUrl
import kotlinx.android.synthetic.main.fragment_question_type2.*

class QuestionType2Fragment(val questionData: Question, val qIn: Int, val qSize: Int) : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var questionType2Binding: FragmentQuestionType2Binding

    private lateinit var answerImgList: Array<ImageView>
    private lateinit var answerTextList: Array<TextView>
    private lateinit var answerChoiceTextList: Array<TextView>
    private lateinit var answerList: ArrayList<String>
    private lateinit var imageList: ArrayList<String>
    private lateinit var shuffleData: ArrayList<ArrayList<String>>
    private var answerChoices: Array<String> = arrayOf("A", "B", "C", "D")

    private fun init(){
        question_type2_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"

        question_type2_fragment_imgAnswer1.setOnClickListener(this)
        question_type2_fragment_imgAnswer2.setOnClickListener(this)
        question_type2_fragment_imgAnswer3.setOnClickListener(this)
        question_type2_fragment_imgAnswer4.setOnClickListener(this)

        answerImgList = arrayOf(
            question_type2_fragment_imgAnswer1,
            question_type2_fragment_imgAnswer2,
            question_type2_fragment_imgAnswer3,
            question_type2_fragment_imgAnswer4
        )

        answerTextList = arrayOf(
            question_type2_fragment_txtAnswer1,
            question_type2_fragment_txtAnswer2,
            question_type2_fragment_txtAnswer3,
            question_type2_fragment_txtAnswer4
        )

        answerChoiceTextList = arrayOf(
            question_type2_fragment_txtChoice1,
            question_type2_fragment_txtChoice2,
            question_type2_fragment_txtChoice3,
            question_type2_fragment_txtChoice4
        )

        shuffleData = AppUtils.shuffleTheAnswersAndImages(questionData.questionAnswers, questionData.questionImages)

        answerList = ArrayList(shuffleData.get(0))
        imageList = ArrayList(shuffleData.get(1))

        setAnswersFromTextsAndImages(answerImgList, answerTextList, answerChoiceTextList, answerList, imageList, answerChoices)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        questionType2Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type2, container, false)
        return questionType2Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.question_type2_fragment_imgAnswer1 -> Singleton.setNextQuestionPage(qSize)
                R.id.question_type2_fragment_imgAnswer2 -> Singleton.setNextQuestionPage(qSize)
                R.id.question_type2_fragment_imgAnswer3 -> Singleton.setNextQuestionPage(qSize)
                R.id.question_type2_fragment_imgAnswer4 -> Singleton.setNextQuestionPage(qSize)
            }
        }
    }

    private fun setAnswersFromTextsAndImages(answerImgList: Array<ImageView>, answerTextList: Array<TextView>, answerChoiceTextList: Array<TextView>, answerList: ArrayList<String>, imageList: ArrayList<String>, answerChoices: Array<String>){
        if (answerTextList.size == answerList.size && answerImgList.size == imageList.size){
            for (aIn in answerTextList.indices){
                answerImgList.get(aIn).downloadImageUrl(imageList.get(aIn))
                answerTextList.get(aIn).text = answerList.get(aIn)
                answerChoiceTextList.get(aIn).text = answerChoices.get(aIn)
            }
        }
    }
}