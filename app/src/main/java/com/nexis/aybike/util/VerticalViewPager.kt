package com.nexis.aybike.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class VerticalViewPager: ViewPager {
    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    private fun init(){
        setPageTransformer(true, VerticalPage())
        overScrollMode = OVER_SCROLL_NEVER
    }

    private fun getIntercambioXY(ev: MotionEvent) : MotionEvent {
        val width = width
        val height = height

        val newX = (ev.y / height) * width
        val newY = (ev.x / width) * height

        ev.setLocation(newX, newY)
        return ev
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val intercepted = super.onInterceptTouchEvent(getIntercambioXY(ev!!))
        getIntercambioXY(ev)
        return intercepted
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(getIntercambioXY(ev!!))
    }

    private class VerticalPage : ViewPager.PageTransformer{
        override fun transformPage(page: View, position: Float) {
            if (position < -1){
                page.alpha = 0f
            }else if (position <= 1){
                page.alpha = 1f
                page.translationX = (page.width * -position)
                val yPosition = position * page.height
                page.translationY = yPosition
            }else{
                page.alpha = 0f
            }
        }
    }
}