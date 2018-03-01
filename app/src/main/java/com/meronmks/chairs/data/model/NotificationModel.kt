package com.meronmks.chairs.data.model

import android.content.Context
import com.meronmks.chairs.R
import com.meronmks.chairs.extensions.toIsoZonedDateTime
import com.sys1yagi.mastodon4j.api.entity.Notification

/**
 * Created by meron on 2018/02/22.
 * MastodonAPIから帰ってくる通知のモデルクラス
 */

class NotificationModel(private val notif : Notification){

    //アクションがあった時刻
    fun createAt(context : Context, now : Long) : String{
        val tootCreateAt = notif.createdAt.toIsoZonedDateTime().toInstant().toEpochMilli()
        val elapsed = (now - tootCreateAt) / 1000
        return when {
            elapsed < 3 ->
                context.getString(R.string.status_now)
            elapsed < 60 ->
                context.getString(R.string.status_second, elapsed)
            elapsed < 3600 ->
                context.getString(R.string.status_min, elapsed / 60)
            elapsed < 3600 * 24 ->
                context.getString(R.string.status_hour, elapsed / (3600))
            else -> context.getString(R.string.status_day, elapsed / (3600 * 24))
        }
    }

    //通知時刻（生データ）
    var tootCreateAt : Long = notif.createdAt.toIsoZonedDateTime().toInstant().toEpochMilli()

    //アクション名（ブースト：reblog　お気に入り：favourite　フォロー：follow）
    val type : String = notif.type

    //アクションを行った人物
    val actionDisplayName : String? = notif.account?.displayName

    //アクションを行った人物のID
    val actionUserName : String? = notif.account?.acct

    //アクションを行った人物のアイコン
    val actionAvater : String? = notif.account?.avatar

    //通知ID
    val id : Long = notif.id

    //どのトゥートに対する通知か（無ければ非表示）
    fun content() : String {
        if (notif.status != null) {
            var content = notif.status!!.content
            for (it in notif.status!!.emojis) {
                content = content.replace(":" + it.shortcode + ":", "<img src=\"" + it.url + "\"/>")
            }
            return content
        }else{
            return ""
        }
    }
}
