package com.meronmks.chairs.data.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by meron on 2017/12/28.
 * Mastodonのアカウントを保存しておくモデルクラス
 */
open class MastodonAccount(
        @PrimaryKey open var id : String = UUID.randomUUID().toString(),
        open var instanceName : String = "",
        open var userName : String = "",
        open var accessToken : String = "",
        open var lastLogin : Date = Date()
) : RealmObject()
