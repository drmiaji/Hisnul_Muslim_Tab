package com.drmiaji.hisnulmuslimtab.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drmiaji.hisnulmuslimtab.MainActivity
import com.drmiaji.hisnulmuslimtab.R
import com.drmiaji.hisnulmuslimtab.fragment.SettingsFragment
import com.drmiaji.hisnulmuslimtab.utils.ThemeUtils
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this) // Apply theme before view inflation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set up the toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val titleTextView = findViewById<TextView>(R.id.toolbar_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        titleTextView.text = getString(R.string.app_name)

        // Tint the back arrow if needed
        val navIconColor = ContextCompat.getColor(this, R.color.nav_icon_color)
        toolbar.navigationIcon?.let { originalDrawable ->
            val wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, navIconColor)
            toolbar.navigationIcon = wrappedDrawable
        }

        if (savedInstanceState == null) {
            val fragment = SettingsFragment().apply {
                onThemeChanged = {
                    restartApp()
                }
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, fragment)
                .commit()
        }
    }

    // Optional: Restart the app cleanly
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finishAffinity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}