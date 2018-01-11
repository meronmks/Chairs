package com.meronmks.chairs.extensions

import android.content.Context
import android.text.Html
import android.text.Spanned

/**
 * Created by meron on 2018/01/11.
 */

fun String.fromHtml(context: Context) : Spanned {
    val result: Spanned
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        result = Html.fromHtml(this,  Html.FROM_HTML_MODE_LEGACY)
    } else {
        result = Html.fromHtml(this)
    }
    return result
}