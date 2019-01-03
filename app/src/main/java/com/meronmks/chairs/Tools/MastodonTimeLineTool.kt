package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.MastodonList
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.method.Accounts
import com.sys1yagi.mastodon4j.api.method.MastodonLists
import com.sys1yagi.mastodon4j.api.method.Public
import com.sys1yagi.mastodon4j.api.method.Timelines
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/01/04.
 * タイムラインを取得する奴
 */
class MastodonTimeLineTool(private val instanceName : String, private val accessToken : String){
    fun getHomeAsync(range: Range = Range()) : Deferred<List<Status>> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val timeLine = Timelines(client)
        return@async timeLine.getHome(range).execute().part
    }

    fun getPublicTLAsync(range: Range = Range()) : Deferred<List<Status>> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val publicTL = Public(client)
        return@async publicTL.getFederatedPublic(range).execute().part
    }

    fun getLocalPublicTLAsync(range: Range = Range()) : Deferred<List<Status>> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val publicTL = Public(client)
        return@async publicTL.getLocalPublic(range).execute().part
    }

    fun getListTLAsync(listID: Long, range: Range = Range()) : Deferred<List<Status>> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val listTL = MastodonLists(client)
        return@async listTL.getListTimeLine(listID, range).execute().part
    }

    fun getListsAsync() : Deferred<List<MastodonList>> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val listTL = MastodonLists(client)
        return@async listTL.getLists().execute().part
    }
}