package com.nandathantsin.ldoebunkercode

import android.app.Application
import androidx.preference.PreferenceManager
import com.nandathantsin.ldoebunkercode.R
import com.nandathantsin.ldoebunkercode.settings.ThemeHelper

class DarkThemeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = prefs.getString(getString(R.string.theme_pref_key), ThemeHelper.DEFAULT_MODE)
        ThemeHelper.applyTheme(themePref!!)
    }
}
