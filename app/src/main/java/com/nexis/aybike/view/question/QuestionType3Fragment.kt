package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType3Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import kotlinx.android.synthetic.main.fragment_question_type3.*

class QuestionType3Fragment(val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var questionType3Binding: FragmentQuestionType3Binding

    private lateinit var answerTextList: Array<TextView>
    private lateinit var answerList: ArrayList<String>
    private lateinit var answerRadioList: Array<ImageView>
    private lateinit var answerLinearList: Array<LinearLayout>
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0

    private fun init(){
        question_type3_fragment_txtQuestionContent.text  = "$qIn-)${questionData.questionContent}"

        question_type3_fragment_linearAnswer1.setOnClickListener(this)
        question_type3_fragment_linearAnswer2.setOnClickListener(this)
        question_type3_fragment_linearAnswer3.setOnClickListener(this)
        question_type3_fragment_linearAnswer4.setOnClickListener(this)

        answerLinearList = arrayOf(
            question_type3_fragment_linearAnswer1,
            question_type3_fragment_linearAnswer2,
            question_type3_fragment_linearAnswer3,
            question_type3_fragment_linearAnswer4
        )

        answerTextList = arrayOf(
            question_type3_fragment_txtAnswer1,
            question_type3_fragment_txtAnswer2,
            question_type3_fragment_txtAnswer3,
            question_type3_fragment_txtAnswer4
        )

        answerRadioList = arrayOf(
            question_type3_fragment_radioAnswer1,
            question_type3_fragment_radioAnswer2,
            question_type3_fragment_radioAnswer3,
            question_type3_fragment_radioAnswer4
        )

        answerList = ArrayList(AppUtils.shuffleTheAnswers(questionData.questionAnswers))
        setAnswersFromText(answerTextList, answerList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!isCreated)
            questionType3Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type3, container, false)

        return questionType3Binding.root
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
                R.id.question_type3_fragment_linearAnswer1 -> selectAnswer(0, subCategoryData, qSize)
                R.id.question_type3_fragment_linearAnswer2 -> selectAnswer(1, subCategoryData, qSize)
                R.id.question_type3_fragment_linearAnswer3 -> selectAnswer(2, subCategoryData, qSize)
                R.id.question_type3_fragment_linearAnswer4 -> selectAnswer(3, subCategoryData, qSize)
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
                            answerLinearList.get(answer).setBackgroundResource(R.drawable.question_type3_correct_bg)
                            answerRadioList.get(answer).setBackgroundResource(R.drawable.question_circle_radio_correct_bg)
                            answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type3CorrectTxtColor))
                        } else
                            setAnswerSelected(answer)
                    } else
                        setAnswerSelected(answer)
                }else {
                    answerLinearList.get(answer).setBackgroundResource(R.drawable.question_type3_bg)
                    answerRadioList.get(answer).setBackgroundResource(R.drawable.question_circle_radio_unselected_bg)
                    answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type3TxtColor))
                }
            }

            Singleton.questionSolvedList[(qIn - 1)] = true
            Singleton.setNextQuestionPage(qSize)
        } else
            "message".show(v, "Testi bitirdikten sonra şıkları değiştiremezsiniz")
    }

    private fun setAnswerSelected(answer: Int){
        answerLinearList.get(answer).setBackgroundResource(R.drawable.question_type3_selected_bg)
        answerRadioList.get(answer).setBackgroundResource(R.drawable.question_circle_radio_selected_bg)
        answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type3SelectedTxtColor))
    }

    private fun setAnswersFromText(answerTextList: Array<TextView>, answerList: ArrayList<String>){
        if (answerTextList.size == answerList.size){
            for (aIn in answerTextList.indices)
                answerTextList.get(aIn).text = answerList.get(aIn)
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
        } else{
            AppUtils.increaseCorrectAndWrongAmount(false)
            setAnswerProperties(true, selectedAIn)
        }
    }

    private fun setAnswerProperties(isCorrect: Boolean, aIn: Int){
        if (isCorrect){
            answerLinearList.get(aIn).setBackgroundResource(R.drawable.question_type3_correct_bg)
            answerRadioList.get(aIn).setBackgroundResource(R.drawable.question_circle_radio_correct_bg)
            answerTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type3CorrectTxtColor))
        } else {
            answerLinearList.get(aIn).setBackgroundResource(R.drawable.question_type3_wrong_bg)
            answerRadioList.get(aIn).setBackgroundResource(R.drawable.question_circle_radio_wrong_bg)
            answerTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type3WrongTxtColor))
        }
    }
}