package com.meronmks.chairs.initialize

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.meronmks.chairs.OAuth.SetInstanceNameActivity
import com.meronmks.chairs.data.database.MastodonAccount
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class MainActivity : AppCompatActivity() {

    lateinit var mRealm : Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        mRealm = Realm.getInstance(realmConfig)
        val accessTokens = readAccessToken()
        if (accessTokens.count() == 0){
            val intent = Intent(this, SetInstanceNameActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    fun readAccessToken() : RealmResults<MastodonAccount> {
        return mRealm.where(MastodonAccount::class.java).findAll()
    }
}
