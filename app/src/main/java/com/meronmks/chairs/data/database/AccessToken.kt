package com.meronmks.chairs.data.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by meron on 2017/12/28.
 * AccessTokenを保存しておくモデルクラス
 */
open class AccessToken(
        @PrimaryKey open var id : String = UUID.randomUUID().toString(),
        @Required open var instanceName : String = "",
        @Required open var accessToken : String = ""
) : RealmObject(){}
