package com.meronmks.chairs.ViewPages.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.meronmks.chairs.R
import com.meronmks.chairs.data.model.TimeLineStatus
import kotlinx.android.synthetic.main.toot_item.view.*

/**
 * Created by meron on 2018/01/09.
 */

class HomeTimeLineAdapter(context: Context) : ArrayAdapter<TimeLineStatus>(context, 0) {
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = layoutInflater.inflate(R.layout.toot_item, parent, false)!!
        }
        val item = getItem(position) as TimeLineStatus

        view.userNameTextView.text = item.userName
        view.tootTextView.text = item.content(context)
        Glide.with(context).load(item.avater).into(view.avatarImageButton)
        return view
    }
}