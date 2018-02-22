package com.meronmks.chairs.data.model

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import com.meronmks.chairs.R
import com.sys1yagi.mastodon4j.api.entity.Status
import com.meronmks.chairs.extensions.fromHtml
import com.meronmks.chairs.extensions.toIsoZonedDateTime

/**
 * Created by meron on 2018/01/04.
 * タイムラインで使うトゥートのモデルクラス
 */

class TimeLineStatus(private val status : Status){

    var offsetMap = mapOf(
            false to 0,
            true to 1
    )

    //トゥートがお気に入りされているかどうか
    var isFavourited: Boolean = status.isFavourited

    //トゥートのお気に入り数
    fun favouritedCount() = (status.favouritesCount + offsetMap.getValue(isFavourited)).takeIf { it > 0 } ?: 0

    //トゥートがブーストされているかどうか
    var isReblogged : Boolean = status.isReblogged

    //トゥートのブースト数
    fun rebloggedCount() = (status.reblogsCount + offsetMap.getValue(isReblogged)).takeIf { it > 0 } ?: 0

    //トゥートがブーストである場合にはブースト元のトゥートを表す
    val reblog: TimeLineStatus? = status.reblog?.let {
        TimeLineStatus(it)
    }

    //トゥートの内容。HTML形式なので変換
    fun content() : String {
        var content = status.content
        for (it in status.emojis) {
            content = content.replace(":" + it.shortcode + ":", "<img src=\"" + it.url + "\"/>")
        }
        return content
    }

    //トゥートされた時刻
    fun createAt(context : Context, now : Long) : String{
        val tootCreateAt = status.createdAt.toIsoZonedDateTime().toInstant().toEpochMilli()
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

    //トゥートの添付メディアが隠されるべきかどうか
    val isSensitive : Boolean = status.isSensitive

    //トゥート本文が隠されるべきかどうか
    val isCW : Boolean = status.spoilerText.isNotEmpty()

    //トゥート本文が隠されるときの最初に表示されるべきテキスト
    val spoilerText : String = status.spoilerText

    //トゥートをした人の画像
    val avater : String = status.account!!.avatar

    //トゥートをした人のユーザー名
    val userName : String = status.account!!.acct

    //トゥートをした人の表示名
    val displayName : String = status.account!!.displayName

    //トゥートのID
    var tootID : Long = status.id

    //トゥート時刻（生データ）
    var tootCreateAt : Long = status.createdAt.toIsoZonedDateTime().toInstant().toEpochMilli()
}
