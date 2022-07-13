package com.nexis.aybike.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    private lateinit var filterArrayAdapter: ArrayAdapter<CharSequence>
    private lateinit var txtSelectedFilter: String

    private fun init(){
        sub_category_content_fragment_recyclerView.setHasFixedSize(true)
        sub_category_content_fragment_recyclerView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        testsAdapter = TestsAdapter(arrayListOf(), subCategory, userId, v)
        sub_category_content_fragment_recyclerView.adapter = testsAdapter

        filterArrayAdapter = ArrayAdapter.createFromResource(v.context, R.array.FilterList, R.layout.spinner_item)
        filterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sub_category_content_fragment_spinnerFilter.adapter = filterArrayAdapter

        sub_category_content_fragment_spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0?.let {
                    txtSelectedFilter = it.getItemAtPosition(p2).toString()
                    setFilter()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                p0?.let {
                    txtSelectedFilter = it.getItemAtPosition(0).toString()
                    setFilter()
                }
            }
        }

        subCategoryContentViewModel = ViewModelProvider(this).get(SubCategoryContentViewModel::class.java)
        observeLiveData()
        subCategoryContentViewModel.getTests(subCategory.subCategoryId, subCategory.subCategoryName, false, "")
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

    private fun setFilter(){
        if (!txtSelectedFilter.equals("-- Seçiniz --") && !txtSelectedFilter.isEmpty())
            subCategoryContentViewModel.getTests(subCategory.subCategoryId, subCategory.subCategoryName, true, txtSelectedFilter)
        else
            subCategoryContentViewModel.getTests(subCategory.subCategoryId, subCategory.subCategoryName, false, txtSelectedFilter)
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
                        subCategory: SubCategory,
                        categoryId: String,
                        testData: Test
                    ) {
                        navDirections = MainFragmentDirections.actionMainFragmentToQuestionsFragment(subCategory, categoryId, testData, userId, null)
                        Navigation.findNavController(v).navigate(navDirections)
                    }
                })
            }
        })
    }
}