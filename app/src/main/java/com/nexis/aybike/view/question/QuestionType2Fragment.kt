package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.nexis.aybike.R
import com.nexis.aybike.adapter.question.QuestionsType2Adapter
import com.nexis.aybike.databinding.FragmentQuestionType2Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import kotlinx.android.synthetic.main.fragment_question_type2.*

class QuestionType2Fragment(val testData: Test, val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment() {
    private lateinit var v: View
    private lateinit var questionType2Binding: FragmentQuestionType2Binding

    private lateinit var questionsType2Adapter: QuestionsType2Adapter
    private lateinit var answerList: ArrayList<String>
    private lateinit var imageList: ArrayList<String>
    private lateinit var shuffleData: ArrayList<ArrayList<String>>
    private val answerChoices: Array<String> = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0
    private var choiceIn: Int = 0

    private fun init(){
        question_type2_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"

        shuffleData = AppUtils.shuffleTheAnswersAndImages(questionData.questionAnswers, questionData.questionImages)
        answerList = ArrayList(shuffleData.get(0))
        imageList = ArrayList(shuffleData.get(1))

        question_type2_fragment_recyclerView.setHasFixedSize(true)
        question_type2_fragment_recyclerView.layoutManager = GridLayoutManager(v.context, 2)
        questionsType2Adapter = QuestionsType2Adapter(subCategoryData?.categoryId, answerList, imageList, answerChoices, qIn, qSize)
        question_type2_fragment_recyclerView.adapter = questionsType2Adapter

        questionsType2Adapter.setTypeOnItemClickListener(object : QuestionsType2Adapter.Type2OnItemClickListener{
            override fun onItemClick(sIn: Int) {
                selectedAIn = sIn

                Singleton.questionSolvedList[(qIn - 1)] = true
                Singleton.setNextQuestionPage((qIn + 1))
            }
        })
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

    fun checkTheAnswer(){
        if (subCategoryData != null){
            if (subCategoryData.categoryId.equals("EntertainmentCategory")){
                choiceIn = getChoiceIn(testData.testEndMessages)
                AppUtils.increaseChoiceAmount(choiceIn)
                questionsType2Adapter.setCheckAnswer(selectedAIn)
            } else {
                if (!answerList.get(selectedAIn).equals(questionData.questionCorrectAnswer)){
                    for (answer in answerList.indices){
                        if (answerList.get(answer).equals(questionData.questionCorrectAnswer)){
                            questionsType2Adapter.setCheckAnswer(answer)
                            return
                        }
                    }
                } else
                    questionsType2Adapter.setCheckAnswer(selectedAIn)
            }
        }
    }

    private fun getChoiceIn(messageList: ArrayList<String>) : Int {
        for (m in messageList.indices){
            if (m == selectedAIn)
                return m
        }

        return 0
    }
}