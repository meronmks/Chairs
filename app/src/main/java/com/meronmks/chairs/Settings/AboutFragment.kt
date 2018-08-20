package com.meronmks.chairs.Settings

import android.os.Bundle
import android.preference.PreferenceFragment
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R

class AboutFragment : PreferenceFragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.about_fragment)
        findPreference("appVersionText").title = "%s Ver%s".format(resources.getString(R.string.app_name), BuildConfig.VERSION_NAME)
        findPreference("appVersionText").summary = "作者:meronmks\r\nPowered by mastodon4j"
    }
}