package com.nandathantsin.ldoebunkercode.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.nandathantsin.ldoebunkercode.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val themePreference = findPreference<ListPreference>(getString(R.string.theme_pref_key))!!
        themePreference.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
            ThemeHelper.applyTheme(newValue as String)
            true
        }
    }
}
