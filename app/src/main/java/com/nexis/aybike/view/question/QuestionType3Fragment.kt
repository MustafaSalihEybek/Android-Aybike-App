package com.nexis.aybike.view.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexis.aybike.R
import com.nexis.aybike.adapter.question.QuestionsType3Adapter
import com.nexis.aybike.databinding.FragmentQuestionType3Binding
import com.nexis.aybike.model.Question
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.AppUtils
import com.nexis.aybike.util.Singleton
import kotlinx.android.synthetic.main.fragment_question_type3.*

class QuestionType3Fragment(val testData: Test, val questionData: Question, val subCategoryData: SubCategory?, val qIn: Int, val qSize: Int) : Fragment() {
    private lateinit var v: View
    private lateinit var questionType3Binding: FragmentQuestionType3Binding

    private lateinit var questionsType3Adapter: QuestionsType3Adapter
    private lateinit var answerList: ArrayList<String>
    private var isCreated: Boolean = false
    private var selectedAIn: Int = 0
    private var choiceIn: Int = 0

    private fun init(){
        question_type3_fragment_txtQuestionContent.text  = "$qIn-)${questionData.questionContent}"
        answerList = ArrayList(AppUtils.shuffleTheAnswers(questionData.questionAnswers))

        question_type3_fragment_recyclerView.setHasFixedSize(true)
        question_type3_fragment_recyclerView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        questionsType3Adapter = QuestionsType3Adapter(subCategoryData?.categoryId, answerList, qIn, qSize)
        question_type3_fragment_recyclerView.adapter = questionsType3Adapter

        questionsType3Adapter.setTypeOnItemClickListener(object : QuestionsType3Adapter.Type3OnItemClickListener{
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
            questionType3Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type3, container, false)

        return questionType3Binding.root
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
                questionsType3Adapter.setCheckAnswer(selectedAIn)
            } else {
                if (!answerList.get(selectedAIn).equals(questionData.questionCorrectAnswer)){
                    for (answer in answerList.indices){
                        if (answerList.get(answer).equals(questionData.questionCorrectAnswer)){
                            questionsType3Adapter.setCheckAnswer(answer)
                            return
                        }
                    }
                } else
                    questionsType3Adapter.setCheckAnswer(selectedAIn)
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