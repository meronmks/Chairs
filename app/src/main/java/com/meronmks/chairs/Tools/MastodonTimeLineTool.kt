package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.method.Accounts
import com.sys1yagi.mastodon4j.api.method.Timelines
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/01/04.
 * タイムラインを取得する奴
 */
class MastodonTimeLineTool(private val instanceName : String, private val accessToken : String){
    fun getHomeAsync(range: Range = Range()) : Deferred<List<Status>> = async(CommonPool){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val timeLine = Timelines(client)
        return@async timeLine.getHome(range).execute().part
    }
}