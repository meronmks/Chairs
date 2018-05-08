package com.meronmks.chairs.Tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

/**
 * https://medium.com/@rajeefmk/android-textview-and-image-loading-from-url-part-1-a7457846abb6 より
 * 2018/04/29　Kotlinに書き直した
 */

open class PicassoImageGetter(context: Context, textView: TextView) : Html.ImageGetter {

    private var textView : TextView? = null
    private var context : Context? = null

    fun PicassoImageGetter(c : Context, target : TextView){
        textView = target
        context = c
    }

    override fun getDrawable(source: String?): Drawable {
        val drawable = BitmapDrawablePlaceHolder()
        if(context == null) return drawable
        Picasso.with(context)
                .load(source)
                .into(drawable)
        return drawable
    }

    private inner class BitmapDrawablePlaceHolder : BitmapDrawable(), Target {

        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            if (drawable != null) {
                drawable!!.draw(canvas)
            }
        }

        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
            if (textView != null) {
                val size = textView!!.getLineHeight()
                drawable.setBounds(0, 0, size, size)
                setBounds(0, 0, size, size)
                textView!!.setText(textView!!.getText())
            }
        }

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            setDrawable(BitmapDrawable(context?.getResources(), bitmap))
        }

        override fun onBitmapFailed(errorDrawable: Drawable) {}

        override fun onPrepareLoad(placeHolderDrawable: Drawable) {

        }

    }
}
