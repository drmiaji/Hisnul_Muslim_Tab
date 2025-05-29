package com.drmiaji.hisnulmuslim.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Settings
import com.drmiaji.hisnulmuslim.R
import com.drmiaji.hisnulmuslim.activity.About
import com.drmiaji.hisnulmuslim.activity.SettingsActivity
import com.drmiaji.hisnulmuslim.models.DrawerItem
import com.drmiaji.hisnulmuslim.models.DrawerMenuGroup

val groupedMenuItems = listOf(
    DrawerMenuGroup(
        groupTitle = "ইউটিউব চ্যানেলসমূহ",
        items = listOf(
            DrawerItem("ড. মু. তাহিরুল কাদেরী", "YouTube", R.drawable.youtube, linkUrl = "https://youtube.com/tahirulqadri"),
            DrawerItem("Dr Miaji|Official", "YouTube", R.drawable.youtube, linkUrl = "https://www.youtube.com/@bmiaji"),
        )
    ),
    DrawerMenuGroup(
        groupTitle = "এবাউট এপ",
        items = listOf(
            DrawerItem("সেটিংস", "এপ নিয়ন্ত্রণ করুন", Icons.Default.Settings, activityClass = SettingsActivity::class.java),
            DrawerItem("এবাউট", "আমাদের সম্পর্কে", Icons.Default.Architecture, activityClass = About::class.java),
        )
    ),
    DrawerMenuGroup(
        groupTitle = "ফেসবুক",
        items = listOf(
            DrawerItem("ড. মিয়াজী", "Facebook", Icons.Default.Facebook, linkUrl = "https://www.facebook.com/batenmiaji2")
        )
    ),
    DrawerMenuGroup(
        groupTitle = "ওয়েবসাইটসমূহ",
        items = listOf(
            DrawerItem("Minhaj-ul-Quran", "Official Site", R.drawable.weblink, linkUrl = "https://www.minhaj.org"),
            DrawerItem("Dr Miaji", "Official Site", R.drawable.weblink, linkUrl = "https://www.drmiaji.com")
        )
    )
)