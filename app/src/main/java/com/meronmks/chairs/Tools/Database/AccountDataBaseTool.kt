package com.meronmks.chairs.Tools.Database

import android.content.Context
import com.meronmks.chairs.data.database.MastodonAccount
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.util.*

/**
 * Created by meron on 2018/01/09.
 * Realmデータベースに対する操作全般
 * かならず最後にClose()メソッドを呼ぶようにすること
 */
class AccountDataBaseTool(context: Context){
    var mRealm : Realm

    init {
        Realm.init(context)
        val realmConfig = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()

        mRealm = Realm.getInstance(realmConfig)
    }

    fun readInstanceName() : String {
        return mRealm.where(MastodonAccount::class.java).findFirst()?.instanceName!!
    }

    fun readAccessToken() : String {
        return mRealm.where(MastodonAccount::class.java).findFirst()?.accessToken!!
    }

    fun readAccounts() : RealmResults<MastodonAccount> {
        return mRealm.where(MastodonAccount::class.java).findAll()
    }

    fun saveAccount(instanceName : String, userName : String, accessToken : String){
        mRealm.executeTransaction {
            val mastodonAccount = mRealm.createObject(MastodonAccount::class.java , UUID.randomUUID().toString())
            mastodonAccount.instanceName = instanceName
            mastodonAccount.userName = userName
            mastodonAccount.accessToken = accessToken
            mastodonAccount.lastLogin = Date()
            mRealm.copyToRealm(mastodonAccount)
        }
        close()
    }

    fun close(){
        mRealm.close()
    }
}