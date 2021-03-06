package com.meronmks.chairs.Images

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.widget.TextView
import com.bumptech.glide.request.transition.Transition

/***
 * https://gist.github.com/extralam/017900f6eb616e9ae97eec9904dd90a6
 * よりすこし改造
 */
class URLImageParser(internal var mContext: Context, internal val mTextView: TextView) : ImageGetter {

    private var targets: ArrayList<Any> = ArrayList()

    override fun getDrawable(url: String): Drawable {
        val urlDrawable = UrlDrawable()
        val load = Glide.with(mContext).asBitmap().load(url)
        val target = BitmapTarget(urlDrawable)
        targets.add(target)
        load.into(target)
        return urlDrawable
    }

    private inner class BitmapTarget(private val urlDrawable: UrlDrawable) : SimpleTarget<Bitmap>() {
        internal lateinit var drawable: Drawable
        override fun onResourceReady(resource: Bitmap, glideAnimation: Transition<in Bitmap>?) {

            drawable = BitmapDrawable(mContext.getResources(), resource)

            mTextView.post {

                val newHeight = mTextView.lineHeight
                val rect = Rect(0, 0, newHeight, newHeight)
                drawable.bounds = rect
                urlDrawable.bounds = rect
                urlDrawable.drawable = drawable
                mTextView.text = mTextView.text
                mTextView.invalidate()
            }

        }
    }

    internal inner class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            if (drawable != null)
                drawable!!.draw(canvas)
        }
    }

}