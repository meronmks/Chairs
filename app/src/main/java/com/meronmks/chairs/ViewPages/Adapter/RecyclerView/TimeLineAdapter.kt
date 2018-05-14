package com.meronmks.chairs.ViewPages.Adapter.RecyclerView

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.meronmks.chairs.Annotation.GlideApp
import com.meronmks.chairs.Annotation.MyAppGlideModule
import com.meronmks.chairs.R
import com.meronmks.chairs.ViewPages.ViewHolder.TimeLineViewHolder
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.MutableLinkMovementMethod
import com.meronmks.chairs.extensions.fromHtml
import kotlinx.android.synthetic.main.toot_item.view.*

/**
 * Created by meron on 2018/03/08.
 */

class TimeLineAdapter(private val context: Context, private val itemClickListener: TimeLineViewHolder.ItemClickListener, private val itemList: ArrayAdapter<TimeLineStatus>): RecyclerView.Adapter<TimeLineViewHolder>() {

    private var mRecyclerView : RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null

    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
       holder?.let {
           var item = itemList.getItem(position)
           //最下段のスペーサー
           it.lastItemMaginSpace.visibility = View.VISIBLE
           if(itemList.count-1 != position) it.lastItemMaginSpace.visibility = View.GONE
           //ブースト関連の処理
           it.rb2Name.visibility = View.GONE
           if (item.reblog != null){
               it.rb2Name.visibility = View.VISIBLE
               it.rb2Name.text = "Reblog by @${item.userName}"
               item = item.reblog!!
           }
           it.displayNameTextView.text = item.displayName
           it.userNameTextView.text = "@${item.userName}"
           it.tootTextView.text = item.content().fromHtml(context, it.tootTextView)
           it.timeTextView.text = item.createAt(context, System.currentTimeMillis())
           it.clientViaTextView.text = "via : ${item.via}"
           GlideApp.with(context).load(item.avater).into(it.avatarImageButton)
           //インライン表示関連の処理（こういうの関数にした方がいいか？）
           it.imageView[0].visibility = View.GONE
           it.imageView[1].visibility = View.GONE
           it.imageView[2].visibility = View.GONE
           it.imageView[3].visibility = View.GONE
           if(item.isMediaAttach){
               var i = 0
               for(media in item.mediaAttachments){
                   it.imageView[i].visibility = View.VISIBLE
                   GlideApp.with(context).load(media.url).into(it.imageView[i])
               }
           }
           //タッチイベントの処理
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.toot_item, parent, false)

        mView.setOnClickListener { view ->
            mRecyclerView?.let {
                itemClickListener.onItemClick(view, it.getChildAdapterPosition(view))
            }
        }

        return TimeLineViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return itemList.count
    }


}