package com.meronmks.chairs.Settings

import android.os.Bundle
import android.support.v14.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.meronmks.chairs.R

class MainPreferenceFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preference_main)
    }
}