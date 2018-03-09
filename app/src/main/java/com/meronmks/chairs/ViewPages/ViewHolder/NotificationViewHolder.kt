package com.meronmks.chairs.ViewPages.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.toot_item.view.*

/**
 * Created by meron on 2018/03/10.
 */
class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    interface ItemClickListener{
        fun onItemClick(view: View, position: Int)
    }

    val lastItemMaginSpace = itemView.lastItemMaginSpace
    val displayNameTextView = itemView.displayNameTextView
    val userNameTextView = itemView.userNameTextView
    val tootTextView = itemView.tootTextView
    val timeTextView = itemView.timeTextView
    val avatarImageButton = itemView.avatarImageButton
}