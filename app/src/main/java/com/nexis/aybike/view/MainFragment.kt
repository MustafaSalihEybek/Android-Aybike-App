package com.nexis.aybike.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import com.nexis.aybike.adapter.CategoriesViewPagerAdapter
import com.nexis.aybike.util.show
import kotlinx.android.synthetic.main.aybike_action_bar.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections

    private lateinit var categoriesAdapter: CategoriesViewPagerAdapter
    private var userId: String? = null

    private fun init(){
        arguments?.let {
            userId = MainFragmentArgs.fromBundle(it).userId

            categoriesAdapter = CategoriesViewPagerAdapter(childFragmentManager)

            categoriesAdapter.addFragment(EntertainmentFragment(userId), "Eğlence")
            categoriesAdapter.addFragment(GeneralCultureFragment(userId), "Genel Kültür")

            main_fragment_viewPager.adapter = categoriesAdapter
            main_fragment_tabLayout.setupWithViewPager(main_fragment_viewPager)

            showActionBarItems()
            aybike_action_bar_imgProfile.setOnClickListener(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.aybike_action_bar_imgProfile -> goToSignPage()
            }
        }
    }

    private fun goToSignPage(){
        if (userId.isNullOrEmpty())
            navDirections = MainFragmentDirections.actionMainFragmentToSignFragment2(true)
        else
            navDirections = MainFragmentDirections.actionMainFragmentToProfileFragment(userId)

            Navigation.findNavController(v).navigate(navDirections)
    }

    private fun showActionBarItems(){
        aybike_action_bar_imgProfile.visibility = View.VISIBLE
    }
}