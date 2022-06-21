package com.nexis.aybike.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SignViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragmentList: ArrayList<Fragment> = ArrayList()

    override fun getCount() = fragmentList.size

    override fun getItem(position: Int) = fragmentList.get(position)

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }
}