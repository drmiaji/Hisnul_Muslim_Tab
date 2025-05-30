package com.drmiaji.hisnulmuslimtab.models

import android.app.Activity

// Data class for drawer items
data class DrawerItem(
    val title: String,
    val subtitle: String = "",
    val icon: Any,
    val activityClass: Class<out Activity>? = null,
    val linkUrl: String? = null
)