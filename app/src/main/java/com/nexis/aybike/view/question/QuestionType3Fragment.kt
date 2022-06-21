package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType3Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.util.AppUtils
import kotlinx.android.synthetic.main.fragment_question_type3.*

class QuestionType3Fragment(val questionData: Question, val qIn: Int) : Fragment() {
    private lateinit var v: View
    private lateinit var questionType3Binding: FragmentQuestionType3Binding

    private lateinit var answerTextList: Array<TextView>
    private lateinit var answerList: ArrayList<String>

    private fun init(){
        question_type3_fragment_txtQuestionContent.text  = "$qIn-)${questionData.questionContent}"

        answerTextList = arrayOf(
            question_type3_fragment_txtAnswer1,
            question_type3_fragment_txtAnswer2,
            question_type3_fragment_txtAnswer3,
            question_type3_fragment_txtAnswer4
        )

        answerList = ArrayList(AppUtils.shuffleTheAnswers(questionData.questionAnswers))
        setAnswersFromText(answerTextList, answerList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        questionType3Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type3, container, false)
        return questionType3Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun setAnswersFromText(answerTextList: Array<TextView>, answerList: ArrayList<String>){
        if (answerTextList.size == answerList.size){
            for (aIn in answerTextList.indices)
                answerTextList.get(aIn).text = answerList.get(aIn)
        }
    }
}