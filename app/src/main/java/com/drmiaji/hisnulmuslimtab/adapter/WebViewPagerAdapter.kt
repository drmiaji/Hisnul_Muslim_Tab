package com.drmiaji.hisnulmuslimtab.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.drmiaji.hisnulmuslimtab.fragment.WebViewFragment

class WebViewPagerAdapter(
    fa: FragmentActivity,
    private val htmlPages: List<String>
) : FragmentStateAdapter(fa) {
    override fun getItemCount() = htmlPages.size
    override fun createFragment(position: Int): Fragment = WebViewFragment.newInstance(htmlPages[position])

    // Add this to return the plain text or HTML for sharing/copying
    fun getPageText(position: Int): String {
        return htmlPages.getOrNull(position) ?: ""
    }
}