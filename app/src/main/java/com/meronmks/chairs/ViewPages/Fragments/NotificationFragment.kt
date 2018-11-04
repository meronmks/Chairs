package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ArrayAdapter
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonNotificationTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.NotificationAdapter
import com.meronmks.chairs.ViewPages.HomeViewPage
import com.meronmks.chairs.Interfaces.ItemClickListener
import com.meronmks.chairs.data.model.NotificationModel
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Notification
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by meron on 2018/01/04.
 * 通知一覧を表示する奴
 */
class NotificationFragment : BaseFragment(), ItemClickListener {
    override fun onItemClick(view: View, position: Int) {
        val item =  itemList.getItem(position)
        (activity as HomeViewPage).showTootDtail(item.id, item.actionAvater!!, item.content(), item.actionUserName)
    }
    lateinit var notification : MastodonNotificationTool
    lateinit var adapter : NotificationAdapter
    lateinit var itemList: ArrayAdapter<NotificationModel>
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        notification = MastodonNotificationTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<NotificationModel>(context,0)
        tootList.adapter = NotificationAdapter(context!!, this, itemList)
        tootList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refreshNotification()
        homeTootListRefresh.setOnRefreshListener {
            refreshNotification(Range(sinceId = itemList.getItem(0).id))
        }
        tootList.addOnScrollListener(InfiniteScrollListener(homeTootList.layoutManager as LinearLayoutManager){
            refreshNotification(Range(maxId = itemList.getItem(itemList.count - 1).id))
        })
        CreateNotificationHandler(itemList)
    }

    private fun refreshNotification(range: Range = Range()) = launch(UI){
        if(loadLock)return@launch
        loadLock = true
        homeTootListRefresh.isRefreshing = true
        val list = getNotification(range)
        list.forEach {
            itemList.add(NotificationModel(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootList.adapter?.notifyDataSetChanged()
        (tootList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(list.size, 0)
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    private suspend fun getNotification(range: Range = Range()): List<Notification> {
        return notification.getNotificationAsync(range).await()
    }
}