package com.meronmks.chairs.ViewPages.ViewHolders

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import kotlinx.android.synthetic.main.toot_item.view.*

/**
 * Created by meron on 2018/03/08.
 */
class TimeLineViewHolder(itemView: View) : ViewHolder(itemView) {

    val displayNameTextView = itemView.displayNameTextView!!
    val userNameTextView = itemView.userNameTextView!!
    val tootTextView = itemView.tootTextView!!
    val timeTextView = itemView.timeTextView!!
    val clientViaTextView = itemView.clientViaTextView!!
    val avatarImageButton = itemView.avatarImageButton!!
    val lastItemMaginSpace = itemView.lastItemMaginSpace!!
    val rb2Name = itemView.rb2Name!!
    val imageView = arrayOf(itemView.imageView1, itemView.imageView2, itemView.imageView3, itemView.imageView4)
    val cwVisibleButton = itemView.cwVisibleButton!!
    val cwTootTextView = itemView.cwTootTextView!!
    val sensitiveText = itemView.sensitiveText!!
}