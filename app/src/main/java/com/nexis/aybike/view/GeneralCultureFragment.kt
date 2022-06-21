package com.nexis.aybike.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nexis.aybike.R
import com.nexis.aybike.adapter.CategoriesViewPagerAdapter
import com.nexis.aybike.model.SubCategory
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.GeneralCultureViewModel
import kotlinx.android.synthetic.main.fragment_general_culture.*

class GeneralCultureFragment(val userId: String?) : Fragment() {
    private lateinit var v: View
    private lateinit var generalCultureViewModel: GeneralCultureViewModel

    private lateinit var categoriesAdapter: CategoriesViewPagerAdapter
    private lateinit var subCategoryList: ArrayList<SubCategory>

    private fun init(){
        categoriesAdapter = CategoriesViewPagerAdapter(childFragmentManager)

        generalCultureViewModel = ViewModelProvider(this).get(GeneralCultureViewModel::class.java)
        observeLiveData()
        generalCultureViewModel.getSubCategories("GeneralCultureCategory")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general_culture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        generalCultureViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        generalCultureViewModel.subCategoryList.observe(viewLifecycleOwner, Observer {
            it?.let {
                subCategoryList = it

                for (category in it)
                    categoriesAdapter.addFragment(SubCategoryContentFragment(category, userId), category.subCategoryName)

                general_culture_fragment_viewPager.adapter = categoriesAdapter
                general_culture_fragment_tabLayout.setupWithViewPager(general_culture_fragment_viewPager)
            }
        })
    }
}