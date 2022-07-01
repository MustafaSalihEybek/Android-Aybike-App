package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType4Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.util.Singleton
import kotlinx.android.synthetic.main.fragment_question_type4.*

class QuestionType4Fragment(val questionData: Question, val qIn: Int, val qSize: Int) : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var questionType4Binding: FragmentQuestionType4Binding

    private fun init(){
        question_type4_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"

        question_type4_fragment_btnAnswerTrue.setOnClickListener(this)
        question_type4_fragment_btnAnswerFalse.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        questionType4Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type4, container, false)
        return questionType4Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.question_type4_fragment_btnAnswerTrue -> Singleton.setNextQuestionPage(qSize)
                R.id.question_type4_fragment_btnAnswerFalse -> Singleton.setNextQuestionPage(qSize)
            }
        }
    }
}