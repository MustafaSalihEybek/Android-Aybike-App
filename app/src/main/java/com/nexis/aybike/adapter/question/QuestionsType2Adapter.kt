package com.nexis.aybike.adapter.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nexis.aybike.R
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.downloadImageUrl

class QuestionsType2Adapter(val categoryId: String?, val answerList: ArrayList<String>, val imageList: ArrayList<String>, val choiceList: Array<String>, val qIn: Int, val qSize: Int) : RecyclerView.Adapter<QuestionsType2Adapter.QuestionsType2Holder>() {
    private lateinit var v: View
    private lateinit var listener: Type2OnItemClickListener
    private var aPos: Int = 0
    private var sIn: Int = -1
    private var cIn: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsType2Holder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.question_type2_item, parent, false)
        return QuestionsType2Holder(v)
    }

    override fun onBindViewHolder(holder: QuestionsType2Holder, position: Int) {
        holder.imgAnswer.downloadImageUrl(imageList.get(position))
        holder.txtAnswer.text = answerList.get(position)
        holder.txtChoice.text = choiceList.get(position)

        if (!Singleton.dataIsSaved){
            if (sIn != -1){
                if (sIn == position){
                    if (categoryId != null){
                        if (categoryId.equals("EntertainmentCategory")){
                            holder.relativeQuestion.setBackgroundResource(R.drawable.question_type2_correct_bg)
                            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
                            holder.txtChoice.setBackgroundResource(R.drawable.question_choice_correct_bg)
                            holder.txtChoice.setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
                        } else
                            setAnswerSelected(holder)
                    } else
                        setAnswerSelected(holder)
                } else {
                    holder.relativeQuestion.setBackgroundResource(R.drawable.question_type2_bg)
                    holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type2TxtColor))
                    holder.txtChoice.setBackgroundResource(R.drawable.question_choice_bg)
                    holder.txtChoice.setTextColor(ContextCompat.getColor(v.context, R.color.type2TxtColor))
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

    inner class QuestionsType2Holder(v: View) : RecyclerView.ViewHolder(v){
        val relativeQuestion: RelativeLayout = v.findViewById(R.id.question_type2_item_relativeQuestion)
        val imgAnswer: ImageView = v.findViewById(R.id.question_type2_item_imgAnswer)
        val txtAnswer: TextView = v.findViewById(R.id.question_type2_item_txtAnswer)
        val txtChoice: TextView = v.findViewById(R.id.question_type2_item_txtChoice)
    }

    interface Type2OnItemClickListener{
        fun onItemClick(sIn: Int)
    }

    fun setTypeOnItemClickListener(listener: Type2OnItemClickListener){
        this.listener = listener
    }

    fun setCheckAnswer(cIn: Int){
        this.cIn = cIn
        notifyDataSetChanged()
    }

    private fun setAnswerProperties(isCorrect: Boolean, holder: QuestionsType2Holder) {
        if (isCorrect){
            holder.relativeQuestion.setBackgroundResource(R.drawable.question_type2_correct_bg)
            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
            holder.txtChoice.setBackgroundResource(R.drawable.question_choice_correct_bg)
            holder.txtChoice.setTextColor(ContextCompat.getColor(v.context, R.color.type2CorrectTxtColor))
        } else {
            holder.relativeQuestion.setBackgroundResource(R.drawable.question_type2_wrong_bg)
            holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type2WrongTxtColor))
            holder.txtChoice.setBackgroundResource(R.drawable.question_choice_wrong_bg)
            holder.txtChoice.setTextColor(ContextCompat.getColor(v.context, R.color.type2WrongTxtColor))
        }
    }

    private fun setAnswerSelected(holder: QuestionsType2Holder){
        holder.relativeQuestion.setBackgroundResource(R.drawable.question_type2_selected_bg)
        holder.txtAnswer.setTextColor(ContextCompat.getColor(v.context, R.color.type2SelectedTxtColor))
        holder.txtChoice.setBackgroundResource(R.drawable.question_choice_selected_bg)
        holder.txtChoice.setTextColor(ContextCompat.getColor(v.context, R.color.type2SelectedTxtColor))
    }
}