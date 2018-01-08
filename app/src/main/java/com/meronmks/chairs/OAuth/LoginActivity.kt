package com.meronmks.chairs.OAuth

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.DataBaseTool
import com.meronmks.chairs.Tools.MastodonAccountTool
import com.meronmks.chairs.Tools.MastodonLoginTool
import com.meronmks.chairs.initialize.MainActivity
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var dataBase : DataBaseTool
    private lateinit var mastodonLogin: MastodonLoginTool
    private lateinit var userName : String
    private lateinit var appRegistration : AppRegistration
    private lateinit var accessToken : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mastodonLogin = MastodonLoginTool(intent.getStringExtra("instanceName"))
        step1TextView.text = "${intent.getStringExtra("instanceName")}${getString(R.string.step1Text)}"
        authCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?)  {
                if(authCodeEditText.text.isNotEmpty()) {
                    dataBase = DataBaseTool(baseContext)
                    launch(UI) {
                        accessToken = mastodonLogin.loginAsync(appRegistration.clientId, appRegistration.clientSecret, authCodeEditText.text.toString()).await().accessToken
                        val account = MastodonAccountTool(appRegistration.instanceName, accessToken)
                        val user = account.getVerifyCredentialsAsync().await()
                        if(user.userName.isEmpty()) return@launch
                        userName = user.userName
                        dataBase.saveAccount(appRegistration.instanceName, userName, accessToken)
                        val intent = Intent(baseContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun startOAuth(view : View) = launch(UI){
        appRegistration = mastodonLogin.registerAppAsync().await()
        val url = mastodonLogin.oAuthAsync(appRegistration.clientId).await()
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }


}
