package com.meronmks.chairs.ViewPages.Adapter.List

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.meronmks.chairs.R
import com.meronmks.chairs.data.model.TimeLineStatus
import kotlinx.android.synthetic.main.toot_item.view.*
import com.meronmks.chairs.extensions.fromHtml


/**
 * Created by meron on 2018/01/09.
 */

class TimeLineAdapter(context: Context?) : ArrayAdapter<TimeLineStatus>(context, 0) {
    val layoutInflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = layoutInflater.inflate(R.layout.toot_item, parent, false)!!
        }
        var item = getItem(position) as TimeLineStatus
        if(count-1 != position)view.lastItemMaginSpace.visibility = View.GONE
        view.rb2Name.visibility = View.GONE
        if (item.reblog != null){
            view.rb2Name.visibility = View.VISIBLE
            view.rb2Name.text = "Reblog by @${item.userName}"
            item = item.reblog!!
        }
        view.displayNameTextView.text = item.displayName
        view.userNameTextView.text = "@${item.userName}"
        view.tootTextView.text = item.content().fromHtml(context, view.tootTextView)
        view.timeTextView.text = item.createAt(context, java.lang.System.currentTimeMillis())
        view.clientViaTextView.text = "Via : ${item.via}"
        Glide.with(context).load(item.avater).into(view.avatarImageButton)
        return view
    }
}