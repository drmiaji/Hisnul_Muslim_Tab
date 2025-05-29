package com.drmiaji.hisnulmuslim.activity

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import com.drmiaji.hisnulmuslim.R
import com.drmiaji.hisnulmuslim.ui.theme.FontManager
import com.google.android.material.appbar.MaterialToolbar

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val titleTextView = findViewById<TextView>(R.id.toolbar_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Add this line to hide the default title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        titleTextView.text = getString(R.string.app_name)
        val typeface = Typeface.createFromAsset(assets, "fonts/solaimanlipi.ttf")
        titleTextView.typeface = typeface

        // Optional: Tint the back arrow (navigation icon)
        val navIconColor = ContextCompat.getColor(this, R.color.nav_icon_color)
        toolbar.navigationIcon?.let { originalDrawable ->
            val wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, navIconColor)
            toolbar.navigationIcon = wrappedDrawable
        }
        // Force menu icon tint color
        toolbar.overflowIcon?.let {
            DrawableCompat.setTint(
                DrawableCompat.wrap(it).mutate(),
                ContextCompat.getColor(this, R.color.nav_icon_color) // use a visible color
            )
        }
        applyCustomFonts()
    }

    private fun applyCustomFonts() {
        // Get your custom fonts
        val solaimanLipiFont = FontManager.getSolaimanLipiTypeface(this)
        FontManager.getQuranArabicTypeface(this)

        // Apply to Button
        findViewById<Button>(R.id.rateUs).apply {
            typeface = solaimanLipiFont
        }

        // Apply to TextViews
        findViewById<TextView>(R.id.appNameText).apply {
            typeface = solaimanLipiFont
        }

        findViewById<TextView>(R.id.aboutUsText).apply {
            typeface = solaimanLipiFont
            // Use quranArabicFont if this TextView contains Arabic text
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mMenuInflater = menuInflater
        mMenuInflater.inflate(R.menu.action_menu, menu)
        menu?.findItem(R.id.action_search)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.share -> {
                val myIntent = Intent(Intent.ACTION_SEND)
                myIntent.setType("text/plain")
                val shareSub: String? = getString(R.string.share_subject)
                val shareBody: String? = getString(R.string.share_message)
                myIntent.putExtra(Intent.EXTRA_TEXT, shareSub)
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(myIntent, "Share using!"))
            }
            R.id.more_apps -> {
                val moreAppsIntent = Intent(Intent.ACTION_VIEW)
                moreAppsIntent.data =
                    "https://play.google.com/store/apps/dev?id=5204491413792621474".toUri()
                startActivity(moreAppsIntent)
            }
            R.id.action_about_us -> {
                startActivity(Intent(this, About::class.java))
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun goToChap(view: View) {
        val id = view.id
        if (id == R.id.rateUs) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
            startActivity(intent)
        } else if (id == R.id.link) {
            val link = Intent(Intent.ACTION_VIEW)
            link.setData("http://www.drmiaji.com".toUri())
            startActivity(link)
        }
    }
}