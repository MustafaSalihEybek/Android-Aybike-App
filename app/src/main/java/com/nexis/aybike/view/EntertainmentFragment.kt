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
import com.nexis.aybike.viewmodel.EntertainmentViewModel
import kotlinx.android.synthetic.main.fragment_entertainment.*

class EntertainmentFragment(val userId: String?) : Fragment() {
    private lateinit var v: View
    private lateinit var entertainmentViewModel: EntertainmentViewModel

    private lateinit var categoriesAdapter: CategoriesViewPagerAdapter
    private lateinit var subCategoryList: ArrayList<SubCategory>

    private fun init(){
        categoriesAdapter = CategoriesViewPagerAdapter(childFragmentManager)

        entertainmentViewModel = ViewModelProvider(this).get(EntertainmentViewModel::class.java)
        observeLiveData()
        entertainmentViewModel.getSubCategories("EntertainmentCategory")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entertainment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        entertainmentViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        entertainmentViewModel.subCategoryList.observe(viewLifecycleOwner, Observer {
            it?.let {
                subCategoryList = it

                for (category in it)
                    categoriesAdapter.addFragment(SubCategoryContentFragment(category, userId), category.subCategoryName)

                entertainment_fragment_viewPager.adapter = categoriesAdapter
                entertainment_fragment_tabLayout.setupWithViewPager(entertainment_fragment_viewPager)
            }
        })
    }
}