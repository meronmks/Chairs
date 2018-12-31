package com.meronmks.chairs.ViewPages.Adapter.RecyclerView

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.meronmks.chairs.R
import com.meronmks.chairs.ViewPages.ViewHolders.TimeLineViewHolder
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.MutableLinkMovementMethod
import com.meronmks.chairs.extensions.fromHtml
import com.bumptech.glide.request.RequestOptions
import com.meronmks.chairs.Images.PhotoViewActivity
import com.meronmks.chairs.Interfaces.ItemClickListener


/**
 * Created by meron on 2018/03/08.
 */

class TimeLineAdapter(private val context: Context, private val itemClickListener: ItemClickListener?, private val itemList: ArrayAdapter<TimeLineStatus>?): RecyclerView.Adapter<TimeLineViewHolder>() {

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
           var item = itemList!!.getItem(position)
           if(item == null) return@let null
           initializeView(it, position)

           val options = RequestOptions()
                   .placeholder(R.drawable.ic_autorenew_black_24dp)
                   .error(R.drawable.ic_error_24dp)

           //ブースト関連の処理
           it.rb2Name.visibility = View.GONE
           if (item.reblog != null){
               it.rb2Name.visibility = View.VISIBLE
               it.rb2Name.text = "Reblog by @${item.userName}"
               item = item.reblog
           }
           it.displayNameTextView.text = item.displayName().fromHtml(context, it.displayNameTextView)
           it.userNameTextView.text = "@${item.userName}"
           it.cwTootTextView.text = item.spoilerText().fromHtml(context, it.cwTootTextView)
           it.tootTextView.text = item.content().fromHtml(context, it.tootTextView)
           it.timeTextView.text = item.createAt(context, System.currentTimeMillis())
           it.clientViaTextView.text = "via : ${item.via}"
           Glide.with(context)
                   .load(item.avater)
                   .apply(options)
                   .into(it.avatarImageButton)

           if(item.isMediaAttach){
               for((i, media) in item.mediaAttachments.withIndex()){
                   it.imageView[i].visibility = View.VISIBLE
                   Glide.with(context)
                           .load(media.previewUrl)
                           .apply(options)
                           .into(it.imageView[i])
               }
           }
           changeVisivleCWText(it, item.isCW)
           it.cwVisibleButton.setOnClickListener {
               changeVisivleCWText(holder, holder.tootTextView.visibility == View.VISIBLE)
           }

           changeNSFWMedia(it, item.isSensitive)
           it.sensitiveText.setOnClickListener{
               changeNSFWMedia(holder, holder.sensitiveText.visibility == View.GONE)
           }

           //タッチイベントの処理
           clickImageViews(it, item)
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

    fun initializeView(it: TimeLineViewHolder, position: Int){
        //最下段のスペーサー
        if(itemList == null) return
        it.lastItemMaginSpace.visibility = View.VISIBLE
        if(itemList.count-1 != position) it.lastItemMaginSpace.visibility = View.GONE
        //インライン表示関連の処理
        it.imageView.forEach {
            it.visibility = View.GONE
        }
        it.cwVisibleButton.visibility = View.GONE
        it.cwTootTextView.visibility = View.GONE
        it.sensitiveText.visibility = View.GONE
    }

    fun changeVisivleCWText(it : TimeLineViewHolder, isCW : Boolean){
        if(isCW){
            it.cwTootTextView.visibility = View.VISIBLE
            it.cwVisibleButton.visibility = View.VISIBLE
            it.tootTextView.visibility = View.GONE
            it.cwVisibleButton.text = "もっと見る"
        }else{
            it.tootTextView.visibility = View.VISIBLE
            it.cwVisibleButton.text = "隠す"
        }
    }

    fun changeNSFWMedia(it: TimeLineViewHolder, isSensitive : Boolean){
        if(isSensitive){
            it.sensitiveText.visibility = View.VISIBLE
        }else{
            it.sensitiveText.visibility = View.GONE
        }
    }

    fun clickImageViews(it: TimeLineViewHolder, item: TimeLineStatus){
        if(item.isMediaAttach){
            for((i, media) in item.mediaAttachments.withIndex()){
                it.imageView[i].setOnClickListener{
                    val intent = Intent(context, PhotoViewActivity::class.java)
                    intent.putExtra("url", media.url)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.toot_item, parent, false)

        if(itemClickListener == null) return TimeLineViewHolder(mView)

        mView.setOnClickListener { view ->
            mRecyclerView?.let {
                itemClickListener.onItemClick(view, it.getChildAdapterPosition(view))
            }
        }

        return TimeLineViewHolder(mView)
    }

    override fun getItemCount(): Int {
        if (itemList == null) return -1
        return itemList.count
    }


}