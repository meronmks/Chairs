package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonNotificationTool
import com.meronmks.chairs.ViewPages.Adapter.List.NotificationAdapter
import com.meronmks.chairs.data.model.NotificationModel
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
class NotificationFragment : Fragment() {

    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var notification : MastodonNotificationTool
    lateinit var adapter : NotificationAdapter
    var loadLock : Boolean = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        notification = MastodonNotificationTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        adapter = NotificationAdapter(context)
        notificationList.adapter = adapter
        refresNotification()
        notificationListRefresh.setOnRefreshListener {
            refresNotification(Range(sinceId = adapter.getItem(0).id))
        }
        notificationList.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                if(notificationList.lastVisiblePosition == adapter.count - 1){
                    refresNotification(Range(maxId = adapter.getItem(adapter.count - 1).id))
                }
            }

            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    fun refresNotification(range: Range = Range()) = launch(UI){
        if(loadLock)return@launch
        loadLock = true
        notificationListRefresh.isRefreshing = true
        val list = getNotification(range)
        list.forEach {
           adapter.add(NotificationModel(it))
        }
        adapter.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
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