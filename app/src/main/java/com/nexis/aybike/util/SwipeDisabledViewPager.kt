package com.nexis.aybike.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SwipeDisabledViewPager(mContext: Context, attrs: AttributeSet) : ViewPager(mContext, attrs) {
    override fun onTouchEvent(ev: MotionEvent?) = false

    override fun onInterceptTouchEvent(ev: MotionEvent?) = false
}