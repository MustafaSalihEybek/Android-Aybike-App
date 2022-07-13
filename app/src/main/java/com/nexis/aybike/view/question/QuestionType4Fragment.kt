package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType4Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import kotlinx.android.synthetic.main.fragment_question_type4.*

class QuestionType4Fragment(val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var questionType4Binding: FragmentQuestionType4Binding

    private lateinit var answerButtonList: Array<Button>
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0

    private fun init(){
        question_type4_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"

        question_type4_fragment_btnAnswerTrue.setOnClickListener(this)
        question_type4_fragment_btnAnswerFalse.setOnClickListener(this)

        answerButtonList = arrayOf(
            question_type4_fragment_btnAnswerTrue,
            question_type4_fragment_btnAnswerFalse
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!isCreated)
            questionType4Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type4, container, false)

        return questionType4Binding.root
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
                R.id.question_type4_fragment_btnAnswerTrue -> selectAnswer(0, subCategoryData, qSize)
                R.id.question_type4_fragment_btnAnswerFalse -> selectAnswer(1, subCategoryData, qSize)
            }
        }
    }

    private fun selectAnswer(aIn: Int, subCategoryData: SubCategory?, qSize: Int){
        if (!Singleton.dataIsSaved){
            selectedAIn = aIn

            for (answer in answerButtonList.indices){
                if (aIn == answer){
                    if (subCategoryData != null){
                        if (subCategoryData.categoryId.equals("EntertainmentCategory")){
                            answerButtonList.get(answer).setBackgroundResource(R.drawable.question_answer_button_correct_bg)
                            answerButtonList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type4CorrectTxtColor))
                        } else
                            setAnswerSelected(answer)
                    } else
                        setAnswerSelected(answer)
                }else {
                    answerButtonList.get(answer).setBackgroundResource(R.drawable.question_answer_button_bg)
                    answerButtonList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type4TxtColor))
                }
            }

            Singleton.questionSolvedList[(qIn - 1)] = true
            Singleton.setNextQuestionPage(qSize)
        } else
            "message".show(v, "Testi bitirdikten sonra şıkları değiştiremezsiniz")
    }

    private fun setAnswerSelected(answer: Int){
        answerButtonList.get(answer).setBackgroundResource(R.drawable.question_answer_button_selected_bg)
        answerButtonList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type4SelectedTxtColor))
    }

    fun checkTheAnswer(){
        if (!answerButtonList.get(selectedAIn).text.toString().uppercase().equals(questionData.questionCorrectAnswer.uppercase())){
            AppUtils.increaseCorrectAndWrongAmount(true)

            for (answer in answerButtonList.indices){
                if (answerButtonList.get(answer).text.toString().uppercase().equals(questionData.questionCorrectAnswer.uppercase())){
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

    private fun setAnswerProperties(isCorrect: Boolean, aIn: Int) {
        if (isCorrect){
            answerButtonList.get(aIn).setBackgroundResource(R.drawable.question_answer_button_correct_bg)
            answerButtonList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type4CorrectTxtColor))
        } else {
            answerButtonList.get(aIn).setBackgroundResource(R.drawable.question_answer_button_wrong_bg)
            answerButtonList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type4WrongTxtColor))
        }
    }
}