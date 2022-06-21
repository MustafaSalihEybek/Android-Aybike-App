package com.nexis.aybike.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexis.aybike.R
import com.nexis.aybike.adapter.TestsAdapter
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.model.Test
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.SubCategoryContentViewModel
import kotlinx.android.synthetic.main.fragment_sub_category_content.*

class SubCategoryContentFragment(val subCategory: SubCategory, val userId: String?) : Fragment() {
    private lateinit var v: View
    private lateinit var subCategoryContentViewModel: SubCategoryContentViewModel
    private lateinit var navDirections: NavDirections

    private lateinit var testList: ArrayList<Test>
    private lateinit var testsAdapter: TestsAdapter

    private fun init(){
        sub_category_content_fragment_recyclerView.setHasFixedSize(true)
        sub_category_content_fragment_recyclerView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        testsAdapter = TestsAdapter(arrayListOf(), subCategory.subCategoryId, subCategory.categoryId)
        sub_category_content_fragment_recyclerView.adapter = testsAdapter

        subCategoryContentViewModel = ViewModelProvider(this).get(SubCategoryContentViewModel::class.java)
        observeLiveData()
        subCategoryContentViewModel.getTests(subCategory.subCategoryId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_category_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        subCategoryContentViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        subCategoryContentViewModel.testList.observe(viewLifecycleOwner, Observer {
            it?.let {
                testList = it
                testsAdapter.loadData(testList)

                testsAdapter.setTestOnItemClickListener(object : TestsAdapter.TestOnItemClickListener{
                    override fun onItemClick(
                        subCategoryId: String,
                        categoryId: String,
                        testData: Test
                    ) {
                        navDirections = MainFragmentDirections.actionMainFragmentToQuestionsFragment(subCategoryId, categoryId, testData, userId)
                        Navigation.findNavController(v).navigate(navDirections)
                    }
                })
            }
        })
    }
}