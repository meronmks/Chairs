package com.meronmks.chairs.Settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonPublicTools
import com.sys1yagi.mastodon4j.api.entity.Instance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InstanceInfoFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.instance_info)
        val accountDataBase = AccountDataBaseTool(context)
        val instance = MastodonPublicTools(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        GlobalScope.launch(Dispatchers.Main){
            val instanceInfo = instance.getInstanceInfo().await()
            findPreference("instanceTitle").summary = instanceInfo.title
            findPreference("instanceVer").summary = instanceInfo.version
            findPreference("instanceDescription").summary = instanceInfo.description
        }
    }

}