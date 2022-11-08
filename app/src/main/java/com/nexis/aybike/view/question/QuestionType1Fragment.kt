package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexis.aybike.R
import com.nexis.aybike.adapter.question.QuestionsType1Adapter
import com.nexis.aybike.databinding.FragmentQuestionType1Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import kotlinx.android.synthetic.main.fragment_question_type1.*

class QuestionType1Fragment(val testData: Test, val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment() {
    private lateinit var v: View
    private lateinit var questionType1Binding: FragmentQuestionType1Binding

    private lateinit var answerList: ArrayList<String>
    private lateinit var questionsType1Adapter: QuestionsType1Adapter
    private val answerChoices: Array<String> = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0
    private var choiceIn: Int = 0

    private fun init(){
        question_type1_fragment_txtQuestionContent.text = "$qIn-)${questionData.questionContent}"
        answerList = ArrayList(AppUtils.shuffleTheAnswers(questionData.questionAnswers))

        question_type1_fragment_recyclerView.setHasFixedSize(true)
        question_type1_fragment_recyclerView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        questionsType1Adapter = QuestionsType1Adapter(subCategoryData?.categoryId, answerList, answerChoices, qIn, qSize)
        question_type1_fragment_recyclerView.adapter = questionsType1Adapter

        questionsType1Adapter.setTypeOnItemClickListener(object : QuestionsType1Adapter.Type1OnItemClickListener{
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
            questionType1Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type1, container, false)

        return questionType1Binding.root
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
                questionsType1Adapter.setCheckAnswer(selectedAIn)
            } else {
                if (!answerList.get(selectedAIn).equals(questionData.questionCorrectAnswer)){
                    for (answer in answerList.indices){
                        if (answerList.get(answer).equals(questionData.questionCorrectAnswer)){
                            questionsType1Adapter.setCheckAnswer(answer)
                            return
                        }
                    }
                } else
                    questionsType1Adapter.setCheckAnswer(selectedAIn)
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