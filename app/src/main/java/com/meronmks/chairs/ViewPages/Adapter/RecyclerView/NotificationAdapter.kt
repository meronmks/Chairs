package com.meronmks.chairs.ViewPages.Adapter.RecyclerView

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.meronmks.chairs.Annotation.GlideApp
import com.meronmks.chairs.R
import com.meronmks.chairs.ViewPages.ViewHolder.NotificationViewHolder
import com.meronmks.chairs.ViewPages.ViewHolder.TimeLineViewHolder
import com.meronmks.chairs.data.model.NotificationModel
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.MutableLinkMovementMethod
import com.meronmks.chairs.extensions.fromHtml

/**
 * Created by meron on 2018/03/10.
 */
class NotificationAdapter(private val context: Context, private val itemClickListener: TimeLineViewHolder.ItemClickListener, private val itemList: ArrayAdapter<NotificationModel>): RecyclerView.Adapter<NotificationViewHolder>() {
    private var mRecyclerView : RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null

    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder?.let {
            val item =itemList.getItem(position)
            //最下段のスペーサー
            it.lastItemMaginSpace.visibility = View.VISIBLE
            if(itemList.count-1 != position) it.lastItemMaginSpace.visibility = View.GONE

            it.displayNameTextView.text = "${item.actionDisplayName} is ${item.type}"
            it.userNameTextView.text = "@${item.actionUserName}"
            it.tootTextView.text = item.content().fromHtml(context, it.tootTextView)
            it.timeTextView.text = item.createAt(context, java.lang.System.currentTimeMillis())
            GlideApp.with(context).load(item.actionAvater).into(it.avatarImageButton)
            //タッチイベントの処理
            it.displayNameTextView.setOnTouchListener { v:View, event: MotionEvent ->
                val textView : TextView = v as TextView
                val m = MutableLinkMovementMethod()
                textView.movementMethod = m
                val mt = m.onTouchEvent(textView, textView.text as Spannable, event)
                textView.movementMethod = null
                textView.isFocusable = false
                return@setOnTouchListener mt
            }
            it.tootTextView.setOnTouchListener { v:View, event: MotionEvent ->
                val textView : TextView = v as TextView
                val m = MutableLinkMovementMethod()
                textView.movementMethod = m
                val mt = m.onTouchEvent(textView, textView.text as Spannable, event)
                textView.movementMethod = null
                textView.isFocusable = false
                return@setOnTouchListener mt
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.notification_item, parent, false)

        mView.setOnClickListener { view ->
            mRecyclerView?.let {
                itemClickListener.onItemClick(view, it.getChildAdapterPosition(view))
            }
        }

        return NotificationViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return itemList.count
    }
}