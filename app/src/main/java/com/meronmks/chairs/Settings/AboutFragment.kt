package com.meronmks.chairs.Settings

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AlertDialog
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import android.webkit.WebView
import android.view.LayoutInflater





class AboutFragment : PreferenceFragment(), Preference.OnPreferenceClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.about_fragment)
        findPreference("appVersionText").title = "%s Ver%s".format(resources.getString(R.string.app_name), BuildConfig.VERSION_NAME)
        findPreference("appVersionText").summary = "作者:meronmks\r\nPowered by mastodon4j"
        findPreference("OSSlist").onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "OSSlist" -> {
                val factory = LayoutInflater.from(activity)
                val inputView = factory.inflate(R.layout.license_dialog, null)
                val builder = AlertDialog.Builder(activity)
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

}