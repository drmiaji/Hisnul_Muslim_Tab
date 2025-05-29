package com.drmiaji.hisnulmuslim.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class WebViewFragment : Fragment() {
    private var webView: WebView? = null

    companion object {
        private const val ARG_HTML = "html_content"
        fun newInstance(html: String): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putString(ARG_HTML, html)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        webView = WebView(requireContext().applicationContext)
        webView?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val htmlContent = arguments?.getString(ARG_HTML) ?: ""
        Log.d("WebViewFragment", "Loading HTML: $htmlContent")

        webView?.settings?.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (!isAdded) return
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    // On Android 10 to 12L (API 29-32), set forceDark (deprecated, but still needed)
                    if (Build.VERSION.SDK_INT in Build.VERSION_CODES.Q..32) {
                        @Suppress("DEPRECATION")
                        webView?.settings?.forceDark = WebSettings.FORCE_DARK_ON
                    }
                    // For all OS versions, you can still inject JS if you want additional dark mode tweaks:
                    webView?.evaluateJavascript(
                        "document.documentElement.classList.add('dark');document.body.classList.add('dark');",
                        null
                    )
                }
            }
        }

        webView?.loadDataWithBaseURL(
            "file:///android_asset/contents/",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
        return webView
    }

    override fun onDestroyView() {
        webView?.webViewClient = WebViewClient() // Release reference safely
        webView = null
        super.onDestroyView()
    }
}