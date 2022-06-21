package com.nexis.aybike.view.sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R
import com.nexis.aybike.adapter.SignViewPagerAdapter
import com.nexis.aybike.util.Singleton
import kotlinx.android.synthetic.main.aybike_action_bar.*
import kotlinx.android.synthetic.main.fragment_sign.*

class SignFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections

    private lateinit var signViewPagerAdapter: SignViewPagerAdapter
    private var signState: Boolean = false

    private fun init(){
        arguments?.let {
            signState = SignFragmentArgs.fromBundle(it).signState

            signViewPagerAdapter = SignViewPagerAdapter(childFragmentManager)

            signViewPagerAdapter.addFragment(SignInFragment(signState))
            signViewPagerAdapter.addFragment(SignUpFragment())

            sign_fragment_viewPager.adapter = signViewPagerAdapter
            Singleton.mViewPager = sign_fragment_viewPager

            showActionBarItems()

            aybike_action_bar_imgClose.setOnClickListener(this)
            aybike_action_bar_imgHome.setOnClickListener(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.aybike_action_bar_imgClose -> goToHomePage()
                R.id.aybike_action_bar_imgHome -> goToHomePage()
            }
        }
    }

    private fun goToHomePage(){
        navDirections = SignFragmentDirections.actionSignFragmentToMainFragment2(null)
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun showActionBarItems(){
        aybike_action_bar_imgClose.visibility = View.VISIBLE
        aybike_action_bar_imgHome.visibility = View.VISIBLE
    }
}