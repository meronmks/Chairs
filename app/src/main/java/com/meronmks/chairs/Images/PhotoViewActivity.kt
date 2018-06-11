package com.meronmks.chairs.Images

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.meronmks.chairs.R
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        supportActionBar?.hide()
        val url = intent.getStringExtra("url")
        val options = RequestOptions()
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_error_24dp)
        Glide.with(baseContext).load(url).apply(options).into(photoView)
    }
}