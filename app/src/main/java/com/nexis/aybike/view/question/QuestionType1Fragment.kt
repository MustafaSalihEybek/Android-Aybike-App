package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType1Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import kotlinx.android.synthetic.main.fragment_question_type1.*

class QuestionType1Fragment(val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var questionType1Binding: FragmentQuestionType1Binding

    private lateinit var answerTextList: Array<TextView>
    private lateinit var answerList: ArrayList<String>
    private var answerChoices: Array<String> = arrayOf("A", "B", "C", "D")
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0

    private fun init(){
        question_type1_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"

        question_type1_fragment_txtAnswer1.setOnClickListener(this)
        question_type1_fragment_txtAnswer2.setOnClickListener(this)
        question_type1_fragment_txtAnswer3.setOnClickListener(this)
        question_type1_fragment_txtAnswer4.setOnClickListener(this)

        answerTextList = arrayOf(
            question_type1_fragment_txtAnswer1,
            question_type1_fragment_txtAnswer2,
            question_type1_fragment_txtAnswer3,
            question_type1_fragment_txtAnswer4
        )

        answerList = ArrayList(AppUtils.shuffleTheAnswers(questionData.questionAnswers))
        setAnswersFromText(answerTextList, answerList, answerChoices)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!isCreated)
            questionType1Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type1, container, false)

        return questionType1Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
        isCreated = true
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.question_type1_fragment_txtAnswer1 -> selectAnswer(0, subCategoryData, qSize)
                R.id.question_type1_fragment_txtAnswer2 -> selectAnswer(1, subCategoryData, qSize)
                R.id.question_type1_fragment_txtAnswer3 -> selectAnswer(2, subCategoryData, qSize)
                R.id.question_type1_fragment_txtAnswer4 -> selectAnswer(3, subCategoryData, qSize)
            }
        }
    }

    private fun selectAnswer(aIn: Int, subCategoryData: SubCategory?, qSize: Int){
        if (!Singleton.dataIsSaved){
            selectedAIn = aIn

            for (answer in answerTextList.indices){
                if (aIn == answer){
                    if (subCategoryData != null){
                        if (subCategoryData.categoryId.equals("EntertainmentCategory")){
                            answerTextList.get(answer).setBackgroundResource(R.drawable.question_type1_correct_bg)
                            answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type1CorrectTxtColor))
                        } else
                            setAnswerSelected(answer)
                    } else
                        setAnswerSelected(answer)
                }else {
                    answerTextList.get(answer).setBackgroundResource(R.drawable.question_type1_bg)
                    answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type1TxtColor))
                }
            }

            Singleton.questionSolvedList[(qIn - 1)] = true
            Singleton.setNextQuestionPage(qSize)
        } else
            "message".show(v, "Testi bitirdikten sonra şıkları değiştiremezsiniz")
    }

    private fun setAnswerSelected(answer: Int){
        answerTextList.get(answer).setBackgroundResource(R.drawable.question_type1_selected_bg)
        answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type1SelectedTxtColor))
    }

    private fun setAnswersFromText(answerTextList: Array<TextView>, answerList: ArrayList<String>, answerChoices: Array<String>){
        if (answerTextList.size == answerList.size){
            for (aIn in answerTextList.indices)
                answerTextList.get(aIn).text = "${answerChoices.get(aIn)}-) ${answerList.get(aIn)}"
        }
    }

    fun checkTheAnswer(){
        if (!answerList.get(selectedAIn).equals(questionData.questionCorrectAnswer)){
            AppUtils.increaseCorrectAndWrongAmount(true)

            for (answer in answerList.indices){
                if (answerList.get(answer).equals(questionData.questionCorrectAnswer)){
                    setAnswerProperties(true, answer)
                    setAnswerProperties(false, selectedAIn)

                    return
                }
            }
        } else {
            AppUtils.increaseCorrectAndWrongAmount(false)
            setAnswerProperties(true, selectedAIn)
        }
    }

    private fun setAnswerProperties(isCorrect: Boolean, aIn: Int) {
        if (isCorrect){
            answerTextList.get(aIn).setBackgroundResource(R.drawable.question_type1_correct_bg)
            answerTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type1CorrectTxtColor))
        } else {
            answerTextList.get(aIn).setBackgroundResource(R.drawable.question_type1_wrong_bg)
            answerTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type1WrongTxtColor))
        }
    }
}