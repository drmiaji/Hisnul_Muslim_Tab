package com.drmiaji.hisnulmuslim.ui.theme

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

object FontManager {
    private var solaimanLipiTypeface: Typeface? = null
    private var quranArabicTypeface: Typeface? = null

    fun getSolaimanLipiTypeface(context: Context): Typeface {
        if (solaimanLipiTypeface == null) {
            solaimanLipiTypeface = Typeface.createFromAsset(context.assets, "fonts/solaimanlipi.ttf")
        }
        return solaimanLipiTypeface!!
    }

    fun getQuranArabicTypeface(context: Context): Typeface {
        if (quranArabicTypeface == null) {
            quranArabicTypeface = Typeface.createFromAsset(context.assets, "fonts/indopak.ttf")
        }
        return quranArabicTypeface!!
    }

    // For Compose
    @Composable
    fun getSolaimanLipiFontFamily(): FontFamily {
        val context = LocalContext.current
        return remember {
            FontFamily(getSolaimanLipiTypeface(context))
        }
    }

    @Composable
    fun getQuranArabicFontFamily(): FontFamily {
        val context = LocalContext.current
        return remember {
            FontFamily(getQuranArabicTypeface(context))
        }
    }
}