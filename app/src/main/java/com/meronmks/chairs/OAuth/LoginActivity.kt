package com.meronmks.chairs.OAuth

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonAccountTool
import com.meronmks.chairs.Tools.MastodonLoginTool
import com.meronmks.chairs.extensions.showToastLogE
import com.meronmks.chairs.initialize.MainActivity
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.tasks.await


class LoginActivity : AppCompatActivity() {

    private lateinit var accountDataBase: AccountDataBaseTool
    private lateinit var mastodonLogin: MastodonLoginTool
    private lateinit var userName : String
    private lateinit var appRegistration : AppRegistration
    private lateinit var accessToken : String
    private lateinit var instanceName : String
    private var clientId : String = ""
    private var clientSecret : String = ""
    private val fireBaseDB : FirebaseFirestore = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LoginButton.isEnabled = false
        instanceName = intent.getStringExtra("instanceName")
        mastodonLogin = MastodonLoginTool(instanceName)
        step1TextView.text = "$instanceName${getString(R.string.step1Text)}"
    }

    fun startOAuth(view: View) = GlobalScope.launch(Dispatchers.Main){
        val document = getMInstanceLists() ?: return@launch
        if(document.exists()) {
            clientId = document.data?.get("clientId").toString()
            clientSecret = document.data?.get("clientSecret").toString()
        }else{
            appRegistration = mastodonLogin.registerAppAsync().await()
            clientId = appRegistration.clientId
            clientSecret = appRegistration.clientSecret
            val instanceData: HashMap<String, Any> = hashMapOf("clientId" to clientId, "clientSecret" to clientSecret)
            addMInstanceLists(instanceData)
        }
        val url = mastodonLogin.oAuthAsync(clientId).await()
        val intent = Intent(Intent.ACTION_VIEW, url)
        LoginButton.isEnabled = true
        startActivity(intent)
    }

    fun login(view: View) = GlobalScope.launch(Dispatchers.Main){
        accountDataBase = AccountDataBaseTool(baseContext)
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cd = cm.primaryClip
        try{
            accessToken = mastodonLogin.loginAsync(clientId, clientSecret, cd.getItemAt(0).text.toString()).await().accessToken
            val account = MastodonAccountTool(instanceName, accessToken)
            val user = account.getVerifyCredentialsAsync().await()
            if(user.userName.isEmpty()) return@launch
            userName = user.userName
            accountDataBase.saveAccount(instanceName, userName, accessToken)
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

    private suspend fun getMInstanceLists(): DocumentSnapshot? {
        return fireBaseDB.collection("MInstanceLists")
                .document(instanceName)
                .get()
                .await()
    }

    private fun addMInstanceLists(instanceData: HashMap<String, Any>){
        fireBaseDB.collection("MInstanceLists")
                .document(instanceName)
                .set(instanceData as Map<String, Any>)
    }
}
