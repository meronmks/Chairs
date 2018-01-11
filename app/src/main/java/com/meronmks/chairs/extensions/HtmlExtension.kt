package com.meronmks.chairs.extensions

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.text.Spannable
import android.widget.TextView
import com.meronmks.chairs.Tools.PicassoImageGetter


/**
 * Created by meron on 2018/01/11.
 */

fun String.fromHtml(context: Context, textView : TextView) : Spanned {
    val result: Spanned
    val imageGetter = PicassoImageGetter(context, textView)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        result = Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
    } else {
        result = Html.fromHtml(this, imageGetter, null) as Spannable
    }
    return result
}