package com.meronmks.chairs.Settings

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import com.meronmks.chairs.R

class MainPreferenceFragment : PreferenceFragment(), Preference.OnPreferenceClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_main)

        findPreference("preferenceAbout").onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "preferenceAbout" -> transitionFragment(AboutFragment())
        }
        return false
    }

    private fun transitionFragment(nextPreferenceFragment: PreferenceFragment) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.settingsFrame, nextPreferenceFragment)
                .commit()
    }

}