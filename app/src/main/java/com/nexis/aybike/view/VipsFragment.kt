package com.nexis.aybike.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.android.billingclient.api.*
import com.nexis.aybike.R
import com.nexis.aybike.model.ShopSub
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.VipsViewModel
import kotlinx.android.synthetic.main.aybike_action_bar.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_vips.*
import java.util.*
import kotlin.collections.ArrayList

class VipsFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections
    private lateinit var vipsViewModel: VipsViewModel

    private lateinit var vipLinearList: Array<LinearLayout>
    private lateinit var vipTitleList: Array<TextView>
    private lateinit var vipContent1List: Array<TextView>
    private lateinit var vipContent2List: Array<TextView>
    private lateinit var vipMonthList: Array<TextView>
    private lateinit var vipPriceList: Array<TextView>
    private var selectedSubIn: Int = 0
    private var userId: String? = null

    private lateinit var shopSubList: ArrayList<ShopSub>
    private lateinit var mBillingClient: BillingClient
    private lateinit var mParams: SkuDetailsParams.Builder
    private lateinit var flowParams: BillingFlowParams
    private lateinit var mSkuList : ArrayList<String>
    private lateinit var selectedShopSub: ShopSub

    private var firstSelect: Boolean = false
    private var firstIn: Int = 0
    private var lastIn: Int = 0

    private fun init(){
        arguments?.let {
            userId = VipsFragmentArgs.fromBundle(it).userId
            aybike_action_bar_imgClose.visibility = View.VISIBLE

            aybike_action_bar_imgClose.setOnClickListener(this)
            vips_fragment_linearVip1.setOnClickListener(this)
            vips_fragment_linearVip2.setOnClickListener(this)
            vips_fragment_linearVip3.setOnClickListener(this)

            vipLinearList = arrayOf(
                vips_fragment_linearVip1,
                vips_fragment_linearVip2,
                vips_fragment_linearVip3
            )

            vipTitleList = arrayOf(
                vips_fragment_txtVip1Title,
                vips_fragment_txtVip2Title,
                vips_fragment_txtVip3Title
            )

            vipContent1List = arrayOf(
                vips_fragment_txtVip1Content1,
                vips_fragment_txtVip2Content1,
                vips_fragment_txtVip3Content1
            )

            vipContent2List = arrayOf(
                vips_fragment_txtVip1Content2,
                vips_fragment_txtVip2Content2,
                vips_fragment_txtVip3Content2
            )

            vipMonthList = arrayOf(
                vips_fragment_txtVip1Month,
                vips_fragment_txtVip2Month,
                vips_fragment_txtVip3Month
            )

            vipPriceList = arrayOf(
                vips_fragment_txtVip1Price,
                vips_fragment_txtVip2Price,
                vips_fragment_txtVip3Price
            )

            vipsViewModel = ViewModelProvider(this).get(VipsViewModel::class.java)
            observeLiveData()
            vipsViewModel.getShopSubs()

            selectedPackage(2)

            val purchaseUpdateListener = PurchasesUpdatedListener {billingResult, mutableList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && mutableList != null){
                    for (purchase in mutableList)
                        handlePurchase(purchase)
                }
            }

            mBillingClient = BillingClient.newBuilder(v.context)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.vips_fragment_btnBuy -> buyVip()
                R.id.aybike_action_bar_imgClose -> backToPage(userId, v)
                R.id.vips_fragment_linearVip1 -> selectedPackage(0)
                R.id.vips_fragment_linearVip2 -> selectedPackage(1)
                R.id.vips_fragment_linearVip3 -> selectedPackage(2)
            }
        }
    }

    private fun handlePurchase(purchase : Purchase?){
        purchase?.let {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED){
                if (!it.isAcknowledged){
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(it.purchaseToken)
                        .build()

                    val acknowledgePurchaseResponseListener =
                        AcknowledgePurchaseResponseListener { p0 ->
                            if (p0.responseCode == BillingClient.BillingResponseCode.OK)
                                vipsViewModel.saveBuyShopSubHistory(selectedShopSub, userId!!, UUID.randomUUID().toString())
                        }

                    mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)
                }
            }
        }
    }

    private fun observeLiveData(){
        vipsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        vipsViewModel.saveHistoryState.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it)
                    vipsViewModel.saveUserData(userId!!, mapOf("userVipStatus" to true))
            }
        })

        vipsViewModel.successMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        vipsViewModel.shopTuple.observe(viewLifecycleOwner, Observer {
            it?.let {
                shopSubList = it.first
                mSkuList = it.second

                mBillingClient.startConnection(object : BillingClientStateListener{
                    override fun onBillingSetupFinished(b0: BillingResult) {
                        if (b0.responseCode == BillingClient.BillingResponseCode.OK){
                            mParams = SkuDetailsParams.newBuilder()
                                .setSkusList(mSkuList).setType(BillingClient.SkuType.SUBS)

                            mBillingClient.querySkuDetailsAsync(mParams.build()
                            ) { p0, p1 ->
                                if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
                                    for (sku in p1.indices) {
                                        flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(p1.get(sku))
                                            .build()

                                        vipPriceList.get(sku).text = flowParams.skuDetails.price
                                    }

                                    vips_fragment_btnBuy.setOnClickListener(this@VipsFragment)
                                }
                            }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        "message".show(v, "Ödeme sistemi şu anda geçerli değil")
                    }
                })
            }
        })
    }

    private fun selectedPackage(sIn: Int){
        selectedSubIn = sIn

        for (sub in vipTitleList.indices){
            if (sub == sIn) {
                if (firstSelect){
                    firstIn = sIn
                    firstSelect = true
                    startQuestionOfDayScaleAnim(true, firstIn)
                } else {
                    lastIn = firstIn
                    startQuestionOfDayScaleAnim(false, lastIn)
                    firstIn = sIn
                    startQuestionOfDayScaleAnim(true, firstIn)
                }

                setSubProperties(sub, true)
            }
            else
                setSubProperties(sub, false)
        }
    }

    private fun startQuestionOfDayScaleAnim(isBigAnim: Boolean, sIn: Int){
        var xScale: Float = 0f
        var yScale: Float = 0f

        if (isBigAnim){
            yScale = 1.15f
            xScale = 1.15f
        }
        else{
            yScale = 1f
            xScale = 1f
        }

        val animatorSet: AnimatorSet = AnimatorSet()

        val objectScaleAnimX: ObjectAnimator = ObjectAnimator.ofFloat(vipLinearList[sIn], "scaleX", xScale)
        objectScaleAnimX.duration = 300

        val objectScaleAnimY: ObjectAnimator = ObjectAnimator.ofFloat(vipLinearList[sIn], "scaleY", yScale)
        objectScaleAnimY.duration = 300

        animatorSet.playTogether(objectScaleAnimX)
        animatorSet.playTogether(objectScaleAnimY)
        animatorSet.start()
    }

    private fun setSubProperties(sIn: Int, isSelected: Boolean){
        if (isSelected){
            vipLinearList.get(sIn).setBackgroundResource(R.drawable.vip_package_selected_bg)
            vipTitleList.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageSelectedTxtColor))
            vipContent1List.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageSelectedTxtColor))
            vipContent2List.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageSelectedTxtColor))
            vipMonthList.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageSelectedTxtColor))
            vipPriceList.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageSelectedTxtColor))
        } else {
            vipLinearList.get(sIn).setBackgroundResource(R.drawable.vip_package_unselected_bg)
            vipTitleList.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageTxtColor))
            vipContent1List.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageTxtColor))
            vipContent2List.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageTxtColor))
            vipMonthList.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageTxtColor))
            vipPriceList.get(sIn).setTextColor(ContextCompat.getColor(v.context, R.color.vipPackageTxtColor))
        }
    }

    private fun buyVip(){
        mParams = SkuDetailsParams.newBuilder()
            .setSkusList(Arrays.asList(mSkuList.get(selectedSubIn))).setType(BillingClient.SkuType.SUBS)

        mBillingClient.querySkuDetailsAsync(mParams.build()
        ) { p0, p1 ->
            if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
                flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(p1.get(0))
                    .build()

                println(p1.get(0))

                selectedShopSub = shopSubList.get(selectedSubIn)
                mBillingClient.launchBillingFlow((v.context as Activity), flowParams)
            }
        }
    }

    private fun backToPage(userId: String?, v: View){
        navDirections = VipsFragmentDirections.actionVipsFragmentToMainFragment(userId)
        Navigation.findNavController(v).navigate(navDirections)
    }
}