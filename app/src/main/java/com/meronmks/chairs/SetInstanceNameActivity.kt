package com.meronmks.chairs

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.*
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import kotlinx.android.synthetic.main.activity_set_instance_name.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient

class SetInstanceNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_instance_name)
    }

    fun addInstance(view: View) = launch(UI){
        val appRegistration = registerApp(instanceNameEditText.text.toString()).await()
        OAuthLogin(instanceNameEditText.text.toString(), appRegistration)
    }

    /**
     * アプリケーション登録
     */
    private fun registerApp(instanceName : String): Deferred<AppRegistration> = async(CommonPool){
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
    private fun OAuthLogin(instanceName: String, appRegistration: AppRegistration) {
        val client: MastodonClient = MastodonClient.Builder(instanceName, OkHttpClient.Builder(), Gson()).build()
        val clientId = appRegistration.clientId
        val apps = Apps(client)

        val urlString = apps.getOAuthUrl(clientId, Scope(Scope.Name.ALL))
        val url = Uri.parse(urlString)
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }
}
