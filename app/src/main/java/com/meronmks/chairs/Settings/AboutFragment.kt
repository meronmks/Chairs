package com.meronmks.chairs.Settings

import android.os.Bundle
import android.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.R.layout.preference
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import android.webkit.WebView
import android.view.LayoutInflater

class AboutFragment : PreferenceFragmentCompat(), androidx.preference.Preference.OnPreferenceClickListener {
    override fun onPreferenceClick(p0: androidx.preference.Preference?): Boolean {
        when (p0?.key) {
            "OSSlist" -> {
                val factory = LayoutInflater.from(activity)
                val inputView = factory.inflate(R.layout.license_dialog, null)
                val builder = AlertDialog.Builder(context!!)
                builder.setTitle("オープンソースライセンス")
                builder.setView(inputView)
                if (inputView != null) {
                    val webView = inputView.findViewById(R.id.webView) as WebView
                    webView.loadUrl("file:///android_asset/licenses.html")
                }
                builder.setPositiveButton("OK") { _, _ -> }
                val dialog = builder.create()
                dialog.show()
            }
        }
        return false
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.about_fragment)
        findPreference("appVersionText").title = "%s Ver%s".format(resources.getString(R.string.app_name), BuildConfig.VERSION_NAME)
        findPreference("appVersionText").summary = "作者:meronmks\r\nPowered by mastodon4j"
        findPreference("OSSlist").onPreferenceClickListener = this
    }
}