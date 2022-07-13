package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nexis.aybike.R
import com.nexis.aybike.databinding.FragmentQuestionType2Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.downloadImageUrl
import com.nexis.aybike.util.show
import kotlinx.android.synthetic.main.fragment_question_type2.*

class QuestionType2Fragment(val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var questionType2Binding: FragmentQuestionType2Binding

    private lateinit var answerImgList: Array<ImageView>
    private lateinit var answerTextList: Array<TextView>
    private lateinit var answerChoiceTextList: Array<TextView>
    private lateinit var answerList: ArrayList<String>
    private lateinit var imageList: ArrayList<String>
    private lateinit var shuffleData: ArrayList<ArrayList<String>>
    private lateinit var relativeQuestionList: Array<RelativeLayout>
    private var answerChoices: Array<String> = arrayOf("A", "B", "C", "D")
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0

    private fun init(){
        question_type2_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"

        question_type2_fragment_relativeQuestion1.setOnClickListener(this)
        question_type2_fragment_relativeQuestion2.setOnClickListener(this)
        question_type2_fragment_relativeQuestion3.setOnClickListener(this)
        question_type2_fragment_relativeQuestion4.setOnClickListener(this)

        relativeQuestionList = arrayOf(
            question_type2_fragment_relativeQuestion1,
            question_type2_fragment_relativeQuestion2,
            question_type2_fragment_relativeQuestion3,
            question_type2_fragment_relativeQuestion4
        )

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
        if (!isCreated)
            questionType2Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type2, container, false)

        return questionType2Binding.root
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
                R.id.question_type2_fragment_relativeQuestion1 -> selectAnswer(0, subCategoryData, qSize)
                R.id.question_type2_fragment_relativeQuestion2 -> selectAnswer(1, subCategoryData, qSize)
                R.id.question_type2_fragment_relativeQuestion3 -> selectAnswer(2, subCategoryData, qSize)
                R.id.question_type2_fragment_relativeQuestion4 -> selectAnswer(3, subCategoryData, qSize)
            }
        }
    }

    private fun selectAnswer(aIn: Int, subCategoryData: SubCategory?, qSize: Int){
        if (!Singleton.dataIsSaved){
            for (answer in answerTextList.indices){
                if (aIn == answer){
                    if (subCategoryData != null){
                        if (subCategoryData.categoryId.equals("EntertainmentCategory")){
                            relativeQuestionList.get(answer).setBackgroundResource(R.drawable.question_type2_correct_bg)
                            answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
                            answerChoiceTextList.get(answer).setBackgroundResource(R.drawable.question_choice_correct_bg)
                            answerChoiceTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
                        } else
                            setAnswerSelected(answer)
                    } else
                        setAnswerSelected(answer)
                }else {
                    relativeQuestionList.get(answer).setBackgroundResource(R.drawable.question_type2_bg)
                    answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type2TxtColor))
                    answerChoiceTextList.get(answer).setBackgroundResource(R.drawable.question_choice_bg)
                    answerChoiceTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type2TxtColor))
                }
            }

            Singleton.questionSolvedList[(qIn - 1)] = true
            Singleton.setNextQuestionPage(qSize)
        } else
            "message".show(v, "Testi bitirdikten sonra şıkları değiştiremezsiniz")
    }

    private fun setAnswerSelected(answer: Int){
        relativeQuestionList.get(answer).setBackgroundResource(R.drawable.question_type2_selected_bg)
        answerTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type2SelectedTxtColor))
        answerChoiceTextList.get(answer).setBackgroundResource(R.drawable.question_choice_selected_bg)
        answerChoiceTextList.get(answer).setTextColor(ContextCompat.getColor(v.context, R.color.type2SelectedTxtColor))
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

    private fun setAnswerProperties(isCorrect: Boolean, aIn: Int) {
        if (isCorrect){
            relativeQuestionList.get(aIn).setBackgroundResource(R.drawable.question_type2_correct_bg)
            answerTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
            answerChoiceTextList.get(aIn).setBackgroundResource(R.drawable.question_choice_correct_bg)
            answerChoiceTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
        } else {
            relativeQuestionList.get(aIn).setBackgroundResource(R.drawable.question_type2_wrong_bg)
            answerTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type2WrongTxtColor))
            answerChoiceTextList.get(aIn).setBackgroundResource(R.drawable.question_choice_wrong_bg)
            answerChoiceTextList.get(aIn).setTextColor(ContextCompat.getColor(v.context, R.color.type2WrongTxtColor))
        }
    }
}