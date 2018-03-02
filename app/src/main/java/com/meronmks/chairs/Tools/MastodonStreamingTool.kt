package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.method.Streaming
import com.sys1yagi.mastodon4j.api.method.Timelines
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/03/02.
 * Streamingのインスタンスを作成する奴
 */
class MastodonStreamingTool(private val instanceName : String, private val accessToken : String){
    fun getStreaming(): Streaming {
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .useStreamingApi()
                .build()
        return Streaming(client)
    }
}