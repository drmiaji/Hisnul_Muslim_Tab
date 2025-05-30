package com.drmiaji.hisnulmuslimtab.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import kotlin.math.abs

class NonScrollableWebView(context: Context, attrs: AttributeSet? = null) : WebView(context, attrs) {
    private var initialX = 0f
    private var initialY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(event.x - initialX)
                val dy = abs(event.y - initialY)

                if (dx > dy) {
                    // Horizontal scroll – let ViewPager2 handle it
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false
                } else {
                    // Vertical scroll – let WebView handle it
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP -> performClick() // Important
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}