package com.drmiaji.hisnulmuslimtab.fragment

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.drmiaji.hisnulmuslimtab.R
import com.drmiaji.hisnulmuslimtab.utils.ThemeUtils

class SettingsFragment : PreferenceFragmentCompat() {

    // This will be set by the Activity
    var onThemeChanged: (() -> Unit)? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>("theme_mode")?.setOnPreferenceChangeListener { _, newValue ->
            ThemeUtils.saveThemeMode(requireContext(), newValue as String)

            // Notify the activity to restart
            onThemeChanged?.invoke()
            true
        }
        findPreference<SwitchPreferenceCompat>("show_transliteration")?.setOnPreferenceChangeListener { _, newValue ->
            // You can log or toast if you want, but no need to save manually,
            // because Preference framework saves it automatically.
            Log.d("Settings", "Transliteration setting changed: $newValue")
            true
        }
    }
}