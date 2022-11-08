package com.nexis.aybike.adapter.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nexis.aybike.R
import com.nexis.aybike.util.Singleton

class QuestionsType1Adapter(val categoryId: String?, val answerList: ArrayList<String>, val choiceList: Array<String>, val qIn: Int, val qSize: Int) : RecyclerView.Adapter<QuestionsType1Adapter.QuestionsType1Holder>() {
    private lateinit var v: View
    private lateinit var listener: Type1OnItemClickListener
    private var aPos: Int = 0
    private var sIn: Int = -1
    private var cIn: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsType1Holder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.question_type1_item, parent, false)
        return QuestionsType1Holder(v)
    }

    override fun onBindViewHolder(holder: QuestionsType1Holder, position: Int) {
        holder.txtAnswer.text = "${choiceList.get(position)}- ${answerList.get(position)}"

        if (!Singleton.dataIsSaved){
            if (sIn != -1){
                if (sIn == position){
                    if (categoryId != null){
                        if (categoryId.equals("EntertainmentCategory")){
                            holder.linearAnswer.setBackgroundResource(R.drawable.question_type1_correct_bg)
                            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type1CorrectTxtColor))
                        } else
                            setAnswerSelected(holder)
                    } else
                        setAnswerSelected(holder)
                } else {
                    holder.linearAnswer.setBackgroundResource(R.drawable.question_type1_bg)
                    holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type1TxtColor))
                }
            }

            if (cIn != -1){
                if (cIn != sIn){
                    setAnswerProperties(true, holder)
                    setAnswerProperties(false, holder)
                } else
                    setAnswerProperties(true, holder)
            }
        }

        holder.itemView.setOnClickListener {
            aPos = holder.adapterPosition

            if (aPos != RecyclerView.NO_POSITION){
                sIn = aPos
                listener.onItemClick(sIn)

                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount() = answerList.size

    inner class QuestionsType1Holder(v: View) : RecyclerView.ViewHolder(v) {
        val txtAnswer: TextView = v.findViewById(R.id.question_type1_item_txtAnswer)
        val linearAnswer: LinearLayout = v.findViewById(R.id.question_type1_item_linearAnswer)
    }

    interface Type1OnItemClickListener{
        fun onItemClick(sIn: Int)
    }

    fun setTypeOnItemClickListener(listener: Type1OnItemClickListener){
        this.listener = listener
    }

    fun setCheckAnswer(cIn: Int){
        this.cIn = cIn
        notifyDataSetChanged()
    }

    private fun setAnswerProperties(isCorrect: Boolean, holder: QuestionsType1Holder){
        if (isCorrect){
            holder.linearAnswer.setBackgroundResource(R.drawable.question_type1_correct_bg)
            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type1CorrectTxtColor))
        } else {
            holder.linearAnswer.setBackgroundResource(R.drawable.question_type1_wrong_bg)
            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type1WrongTxtColor))
        }
    }

    private fun setAnswerSelected(holder: QuestionsType1Holder){
        holder.linearAnswer.setBackgroundResource(R.drawable.question_type1_selected_bg)
        holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type1SelectedTxtColor))
    }
}