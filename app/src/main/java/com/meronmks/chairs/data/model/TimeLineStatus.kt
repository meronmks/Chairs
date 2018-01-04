package com.meronmks.chairs.data.model

import com.sys1yagi.mastodon4j.api.entity.Context
import com.sys1yagi.mastodon4j.api.entity.Status

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
    fun favouritedCount() = (status.favouritesCount + offsetMap.getValue(isFavourited)).takeIf { it > 0 }?.toString() ?: ""

    //トゥートがブーストされているかどうか
    var isReblogged : Boolean = status.isReblogged

    //トゥートのブースト数
    fun rebloggedCount() = (status.reblogsCount + offsetMap.getValue(isReblogged)).takeIf { it > 0 }?.toString() ?: ""

    //トゥートがブーストである場合にはブースト元のトゥートを表す
    val reblog: TimeLineStatus? = status.reblog?.let {
        TimeLineStatus(it)
    }

    //トゥートの内容。HTML形式
    fun content() : String{
        return status.content
    }

    //トゥートされた時刻
    fun createAt(context : Context, now : Long) : String{
        val tootCreateAt = status.createdAt
        return  tootCreateAt
    }

    //トゥートの添付メディアが隠されるべきかどうか
    val isSensitive : Boolean = status.isSensitive

    //トゥート本文が隠されるべきかどうか
    val isCW : Boolean = status.spoilerText.isNotEmpty()

    //トゥート本文が隠されるときの最初に表示されるべきテキスト
    val spoilerText : String = status.spoilerText
}
