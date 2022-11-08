package com.nexis.aybike.adapter.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nexis.aybike.R
import com.nexis.aybike.util.Singleton

class QuestionsType3Adapter(val categoryId: String?, val answerList: ArrayList<String>, val qIn: Int, val qSize: Int) : RecyclerView.Adapter<QuestionsType3Adapter.QuestionsType3Holder>() {
    private lateinit var v: View
    private lateinit var listener: Type3OnItemClickListener
    private var aPos: Int = 0
    private var sIn: Int = -1
    private var cIn: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsType3Holder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.question_type3_item, parent, false)
        return QuestionsType3Holder(v)
    }

    override fun onBindViewHolder(holder: QuestionsType3Holder, position: Int) {
        holder.txtAnswer.text = answerList.get(position)

        if (!Singleton.dataIsSaved){
            if (sIn != -1){
                if (sIn == position){
                    if (categoryId != null){
                        if (categoryId.equals("EntertainmentCategory")){
                            holder.linearAnswer.setBackgroundResource(R.drawable.question_type3_correct_bg)
                            holder.radioAnswer.setBackgroundResource(R.drawable.question_circle_radio_correct_bg)
                            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type3CorrectTxtColor))
                        } else
                            setAnswerSelected(holder)
                    } else
                        setAnswerSelected(holder)
                } else {
                    holder.linearAnswer.setBackgroundResource(R.drawable.question_type3_bg)
                    holder.radioAnswer.setBackgroundResource(R.drawable.question_circle_radio_unselected_bg)
                    holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type3TxtColor))
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

    inner class QuestionsType3Holder(v: View) : RecyclerView.ViewHolder(v){
        val linearAnswer: LinearLayout = v.findViewById(R.id.question_type3_item_linearAnswer)
        val radioAnswer: ImageView = v.findViewById(R.id.question_type3_fragment_radioAnswer)
        val txtAnswer: TextView = v.findViewById(R.id.question_type3_fragment_txtAnswer)
    }

    interface Type3OnItemClickListener{
        fun onItemClick(sIn: Int)
    }

    fun setTypeOnItemClickListener(listener: Type3OnItemClickListener){
        this.listener = listener
    }

    fun setCheckAnswer(cIn: Int){
        this.cIn = cIn
        notifyDataSetChanged()
    }

    private fun setAnswerProperties(isCorrect: Boolean, holder: QuestionsType3Holder){
        if (isCorrect){
            holder.linearAnswer.setBackgroundResource(R.drawable.question_type3_correct_bg)
            holder.radioAnswer.setBackgroundResource(R.drawable.question_circle_radio_correct_bg)
            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type3CorrectTxtColor))
        } else {
            holder.linearAnswer.setBackgroundResource(R.drawable.question_type3_wrong_bg)
            holder.radioAnswer.setBackgroundResource(R.drawable.question_circle_radio_wrong_bg)
            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type3WrongTxtColor))
        }
    }

    private fun setAnswerSelected(holder: QuestionsType3Holder){
        holder.linearAnswer.setBackgroundResource(R.drawable.question_type3_selected_bg)
        holder.radioAnswer.setBackgroundResource(R.drawable.question_circle_radio_selected_bg)
        holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type3SelectedTxtColor))
    }
}