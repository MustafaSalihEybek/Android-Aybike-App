package com.nexis.aybike.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nexis.aybike.R
import com.nexis.aybike.databinding.TestItemBinding
import com.nexis.aybike.model.Test

class TestsAdapter(var testList: ArrayList<Test>, val subCategoryId: String, val categoryId: String) : RecyclerView.Adapter<TestsAdapter.TestsHolder>() {
    private lateinit var v: TestItemBinding
    private lateinit var testOnItemClickListener: TestOnItemClickListener
    private var aPos: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestsHolder {
        v = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.test_item, parent, false)
        return TestsHolder(v)
    }

    override fun onBindViewHolder(holder: TestsHolder, position: Int) {
        holder.tH.test = testList.get(position)

        holder.itemView.setOnClickListener {
            aPos = holder.adapterPosition

            if (aPos != RecyclerView.NO_POSITION)
                testOnItemClickListener.onItemClick(subCategoryId, categoryId, testList.get(aPos))
        }
    }

    override fun getItemCount() = testList.size

    inner class TestsHolder(var tH: TestItemBinding) : RecyclerView.ViewHolder(tH.root)

    fun loadData(tests: ArrayList<Test>){
        testList = tests
        notifyDataSetChanged()
    }

    interface TestOnItemClickListener{
        fun onItemClick(subCategoryId: String, categoryId: String, testData: Test)
    }

    fun setTestOnItemClickListener(testOnItemClickListener: TestOnItemClickListener){
        this.testOnItemClickListener = testOnItemClickListener
    }
}