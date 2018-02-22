package com.meronmks.chairs.ViewPages.Adapter.List

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.meronmks.chairs.R
import com.meronmks.chairs.data.model.NotificationModel
import kotlinx.android.synthetic.main.toot_item.view.*

/**
 * Created by meron on 2018/02/22.
 */
class NotificationAdapter(context: Context) : ArrayAdapter<NotificationModel>(context, 0) {
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = layoutInflater.inflate(R.layout.toot_item, parent, false)!!
        }
        val item = getItem(position) as NotificationModel
        view.displayNameTextView.text = "${item.actionDisplayName} is ${item.type}"
        view.userNameTextView.text = "@${item.actionUserName}"
//        view.tootTextView.text = item.content().fromHtml(context, view.tootTextView)
        view.timeTextView.text = item.createAt(context, java.lang.System.currentTimeMillis())
        Glide.with(context).load(item.actionAvater).into(view.avatarImageButton)
        return view
    }
}