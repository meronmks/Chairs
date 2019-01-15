package com.meronmks.chairs.Tools

import android.net.Uri
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Scope
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.OkHttpClient

/**
 * Created by meron on 2018/01/04.
 * Mastodonへログインする
 */

class MastodonLoginTool(private val instanceName: String){

    /**
     * アプリケーション登録
     */
    fun registerAppAsync(): Deferred<AppRegistration> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson()).build()
        val apps = Apps(client)
        return@async apps.createApp(
                clientName = "Chairs",
                redirectUris = "urn:ietf:wg:oauth:2.0:oob",
                scope = Scope(Scope.Name.ALL),
                website = "https://github.com/meronmks/Chairs"
        ).execute()
    }

    /**
     * OAuth認証
     */
    fun oAuthAsync(clientId : String) :Deferred<Uri> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson()).build()
        val apps = Apps(client)

        val urlString = apps.getOAuthUrl(clientId, Scope(Scope.Name.ALL))
        return@async Uri.parse(urlString)
    }

    /**
     * Login処理
     */
    fun loginAsync(clientId: String, clientSecret : String, authCode : String) : Deferred<AccessToken> = GlobalScope.async(Dispatchers.Default){
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson()).build()
        val apps = Apps(client)
        return@async apps.getAccessToken(clientId, clientSecret, code = authCode).execute()
    }
}
