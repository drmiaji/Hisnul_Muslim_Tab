package com.drmiaji.hisnulmuslim.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.drmiaji.hisnulmuslim.R
import com.drmiaji.hisnulmuslim.utils.ThemeUtils

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Keep your existing theme preference change listener
        findPreference<ListPreference>("theme_mode")?.setOnPreferenceChangeListener { _, newValue ->
            ThemeUtils.saveThemeMode(requireContext(), newValue as String)
            // No need to recreate the activity here since the theme is applied in onResume
            true
        }
    }
}