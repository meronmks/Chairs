package com.meronmks.chairs.extensions

import android.text.Selection
import android.view.MotionEvent
import android.R.attr.onClick
import android.net.Uri
import android.text.style.URLSpan
import android.text.style.ClickableSpan
import android.text.Layout
import android.text.Spannable
import android.widget.TextView
import android.text.method.LinkMovementMethod
import com.meronmks.chairs.Interfaces.OnUrlClickListener

/**
 * Htmlテキストのリンククリックアクションをオーバーライドするためのクラス。<br>
 *
 * original source is android.text.method.LinkMovementMethod.java
 * http://oigami.hatenablog.com/entry/2014/11/08/082615
 * @author S.Kamba
 *
 */
class MutableLinkMovementMethod : LinkMovementMethod() {

    /** Urlクリックリスナー  */
    private var listener: OnUrlClickListener? = null

    /*
   * Urlクリックリスナーを登録
   */
    fun setOnUrlClickListener(l: OnUrlClickListener) {
        listener = l
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {

        // LinkMovementMethod#onTouchEventそのまんま

        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(off, off, ClickableSpan::class.java)

            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    // リスナーがあればそちらを呼び出し
                    if (link[0] is URLSpan && listener != null) {
                        val uri = Uri.parse((link[0] as URLSpan).url)
                        listener!!.onUrlClick(widget, uri)
                    } else {
                        link[0].onClick(widget)
                    }
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]))
                }

                return true
            } else {
                Selection.removeSelection(buffer)
            }
        }

        return false//super.onTouchEvent(widget, buffer, event);
    }
}