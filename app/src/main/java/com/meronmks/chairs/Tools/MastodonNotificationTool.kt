package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.method.Notifications
import kotlinx.coroutines.*
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/02/22.
 * 通知に関するもの
 */
class MastodonNotificationTool(private val instanceName : String, private val accessToken : String){
    fun getNotificationAsync(range: Range = Range()) : Deferred<List<Notification>> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val notif = Notifications(client)
        return@async notif.getNotifications(range).execute().part
    }
}