package com.meronmks.chairs.Tools

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.Account
import com.sys1yagi.mastodon4j.api.method.Accounts
import kotlinx.coroutines.*
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/01/04.
 * アカウントの処理
 */

class MastodonAccountTool(private val instanceName : String, private val accessToken : String){
    /**
     * ログインユーザー取得（と同時にアクセストークンが正しいかを調べる）
     */
    fun getVerifyCredentialsAsync() : Deferred<Account> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken)
                .build()
        val accounts = Accounts(client)
        return@async accounts.getVerifyCredentials().execute()
    }
}