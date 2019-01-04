package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.Instance
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.method.Public
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient

class MastodonPublicTools(private val instanceName : String, private val accessToken : String){
    fun getInstanceInfo(): Deferred<Instance> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val public = Public(client)
        return@async public.getInstance().execute()
    }
}