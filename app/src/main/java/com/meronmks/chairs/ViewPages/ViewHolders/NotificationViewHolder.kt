package com.meronmks.chairs.ViewPages.ViewHolders

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import kotlinx.android.synthetic.main.toot_item.view.*

/**
 * Created by meron on 2018/03/10.
 */
class NotificationViewHolder(itemView: View) : ViewHolder(itemView) {
    val lastItemMaginSpace = itemView.lastItemMaginSpace
    val displayNameTextView = itemView.displayNameTextView
    val userNameTextView = itemView.userNameTextView
    val tootTextView = itemView.tootTextView
    val timeTextView = itemView.timeTextView
    val avatarImageButton = itemView.avatarImageButton
}