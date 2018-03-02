package com.meronmks.chairs.OAuth

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonAccountTool
import com.meronmks.chairs.Tools.MastodonLoginTool
import com.meronmks.chairs.initialize.MainActivity
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException


class LoginActivity : AppCompatActivity() {

    private lateinit var accountDataBase: AccountDataBaseTool
    private lateinit var mastodonLogin: MastodonLoginTool
    private lateinit var userName : String
    private lateinit var appRegistration : AppRegistration
    private lateinit var accessToken : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LoginButton.isEnabled = false
        mastodonLogin = MastodonLoginTool(intent.getStringExtra("instanceName"))
        step1TextView.text = "${intent.getStringExtra("instanceName")}${getString(R.string.step1Text)}"
    }

    fun startOAuth(view: View) = launch(UI){
        appRegistration = mastodonLogin.registerAppAsync().await()
        val url = mastodonLogin.oAuthAsync(appRegistration.clientId).await()
        val intent = Intent(Intent.ACTION_VIEW, url)
        LoginButton.isEnabled = true
        startActivity(intent)
    }

    fun login(view: View) = launch(UI){
        accountDataBase = AccountDataBaseTool(baseContext)
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cd = cm.primaryClip
        try{
            accessToken = mastodonLogin.loginAsync(appRegistration.clientId, appRegistration.clientSecret, cd.getItemAt(0).text.toString()).await().accessToken
            val account = MastodonAccountTool(appRegistration.instanceName, accessToken)
            val user = account.getVerifyCredentialsAsync().await()
            if(user.userName.isEmpty()) return@launch
            userName = user.userName
            accountDataBase.saveAccount(appRegistration.instanceName, userName, accessToken)
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }catch (e: Mastodon4jRequestException){
            if(e.response?.code() == 401){
                getString(R.string.AuthenticationFaild).showToastLogE(baseContext)
            }
        }
        catch (e: Exception){
            e.message.toString().showToastLogE(baseContext)
        }
    }
}
