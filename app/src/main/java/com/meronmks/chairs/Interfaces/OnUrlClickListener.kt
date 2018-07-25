package com.meronmks.chairs.Interfaces

import android.net.Uri
import android.widget.TextView

/**
 * Urlのリンクをタップした時のイベントを受け取るリスナー
 *
 */
interface OnUrlClickListener {
    fun onUrlClick(widget: TextView, uri: Uri)
}