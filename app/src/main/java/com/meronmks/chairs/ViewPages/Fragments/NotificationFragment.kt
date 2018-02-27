package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.DataBaseTool
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

    lateinit var dataBase : DataBaseTool
    lateinit var notification : MastodonNotificationTool
    lateinit var adapter : NotificationAdapter
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataBase = DataBaseTool(context)
        notification = MastodonNotificationTool(dataBase.readInstanceName(), dataBase.readAccessToken())
        adapter = NotificationAdapter(context)
        notificationList.adapter = adapter
        refresNotification()
        notificationListRefresh.setOnRefreshListener {
            refresNotification(Range(sinceId = adapter.getItem(0).id))
        }
    }

    fun refresNotification(range: Range = Range()) = launch(UI){
        notificationListRefresh.isRefreshing = true
        val list = getNotification(range)
        list.forEach {
           adapter.add(NotificationModel(it))
        }
        adapter.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        notificationListRefresh.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBase.close()
    }

    suspend fun getNotification(range: Range = Range()): List<Notification> {
        return notification.getNotificationAsync(range).await()
    }
}