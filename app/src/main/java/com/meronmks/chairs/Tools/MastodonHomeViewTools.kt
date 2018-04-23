package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.method.Statuses
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/02/17.
 */

class MastodonHomeViewTools(private val instanceName : String, private val accessToken : String){

    var statuses: Statuses? = null

    fun tootAsync(tootText : String, inReplyToId : Long?, mediaIds: List<Long>?, sensitive : Boolean, spoilerText: String?, visibility: Status.Visibility) = async(CommonPool){
        if(statuses == null) {
            val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                    .accessToken(accessToken)
                    .build()
            statuses = Statuses(client)
        }
        return@async statuses?.postStatus(tootText, inReplyToId, mediaIds, sensitive, spoilerText, visibility)?.execute()
    }

    fun postAsyncReblog(statusID : Long) = async(CommonPool){
        if(statuses == null) {
            val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                    .accessToken(accessToken)
                    .build()
            statuses = Statuses(client)
        }
        return@async statuses?.postReblog(statusID)?.execute()
    }
}