package com.meronmks.chairs.Settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import java.security.InvalidParameterException

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        val fragment = when (pref?.fragment) {
            AboutFragment::class.java.name -> AboutFragment()
            InstanceInfoFragment::class.java.name -> InstanceInfoFragment()
            else -> throw InvalidParameterException("Invalid fragment, fragment name is " + pref?.fragment)
        }
        fragment.setTargetFragment(caller, 0)
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, MainPreferenceFragment())
                .commit()
    }
}