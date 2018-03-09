package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonNotificationTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.NotificationAdapter
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.TimeLineAdapter
import com.meronmks.chairs.ViewPages.ViewHolder.TimeLineViewHolder
import com.meronmks.chairs.data.model.NotificationModel
import com.meronmks.chairs.data.model.TimeLineStatus
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Notification
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by meron on 2018/01/04.
 * 通知一覧を表示する奴
 */
class NotificationFragment : Fragment(), TimeLineViewHolder.ItemClickListener  {
    override fun onItemClick(view: View, position: Int) {

    }
    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var notification : MastodonNotificationTool
    lateinit var adapter : NotificationAdapter
    lateinit var itemList: ArrayAdapter<NotificationModel>
    var loadLock : Boolean = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        notification = MastodonNotificationTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<NotificationModel>(context,0)
        notificationList.adapter = NotificationAdapter(context!!, this, itemList)
        notificationList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refresNotification()
        notificationListRefresh.setOnRefreshListener {
            refresNotification(Range(sinceId = itemList.getItem(0).id))
        }
        notificationList.addOnScrollListener(InfiniteScrollListener(notificationList.layoutManager as LinearLayoutManager){
            refresNotification(Range(maxId = itemList.getItem(itemList.count - 1).id))
        })
    }

    fun refresNotification(range: Range = Range()) = launch(UI){
        if(loadLock)return@launch
        loadLock = true
        notificationListRefresh.isRefreshing = true
        val list = getNotification(range)
        list.forEach {
            itemList.add(NotificationModel(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        notificationList.adapter.notifyDataSetChanged()
        notificationListRefresh.isRefreshing = false
        loadLock = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase.close()
    }

    suspend fun getNotification(range: Range = Range()): List<Notification> {
        return notification.getNotificationAsync(range).await()
    }

    fun listScroll2Top(){
        notificationList.smoothScrollToPosition(0)
    }
}