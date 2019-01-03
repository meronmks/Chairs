package com.meronmks.chairs.Settings

import android.os.Bundle
import android.preference.Preference
import android.support.v14.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import com.meronmks.chairs.R

class MainPreferenceFragment : PreferenceFragmentCompat(), android.support.v7.preference.Preference.OnPreferenceClickListener {
    override fun onPreferenceClick(p0: android.support.v7.preference.Preference?): Boolean {
        when (p0?.key) {
            "preferenceAbout" -> transitionFragment(AboutFragment())
        }
        return false
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_main)

        findPreference("preferenceAbout").onPreferenceClickListener = this
    }

    private fun transitionFragment(nextPreferenceFragment: PreferenceFragment) {
        fragmentManager?.beginTransaction()?.addToBackStack(null)?.replace(R.id.settingsFrame, nextPreferenceFragment as Fragment)?.commit()
    }

}