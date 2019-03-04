package com.meronmks.chairs.Settings

import android.os.Bundle
import androidx.preference.PreferenceFragment
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.meronmks.chairs.R

class MainPreferenceFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preference_main)
    }
}