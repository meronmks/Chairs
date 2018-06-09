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
import com.meronmks.chairs.Tools.MastodonStreamingTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.NotificationAdapter
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.TimeLineAdapter
import com.meronmks.chairs.ViewPages.HomeViewPage
import com.meronmks.chairs.ViewPages.ViewHolder.TimeLineViewHolder
import com.meronmks.chairs.data.model.NotificationModel
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.StreamingAsyncTask
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
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
        val item =  itemList.getItem(position)
        (activity as HomeViewPage).showTootDtail(item.id, item.actionAvater!!, item.content(), item.actionUserName)
    }
    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var notification : MastodonNotificationTool
    lateinit var adapter : NotificationAdapter
    lateinit var itemList: ArrayAdapter<NotificationModel>
    var shutdownable : Shutdownable? = null
    var loadLock : Boolean = false
    private val tootlist by lazy { notificationList }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        notification = MastodonNotificationTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<NotificationModel>(context,0)
        tootlist.adapter = NotificationAdapter(context!!, this, itemList)
        tootlist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refresNotification()
        notificationListRefresh.setOnRefreshListener {
            refresNotification(Range(sinceId = itemList.getItem(0).id))
        }
        tootlist.addOnScrollListener(InfiniteScrollListener(notificationList.layoutManager as LinearLayoutManager){
            refresNotification(Range(maxId = itemList.getItem(itemList.count - 1).id))
        })
        CreateHandler()
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
        tootlist.adapter.notifyDataSetChanged()
        (tootlist.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(list.size, 0)
        notificationListRefresh.isRefreshing = false
        loadLock = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase?.close()
        shutdownable?.shutdown()
    }

    suspend fun getNotification(range: Range = Range()): List<Notification> {
        return notification.getNotificationAsync(range).await()
    }

    fun CreateHandler(){
        val handler = object : com.sys1yagi.mastodon4j.api.Handler{
            override fun onStatus(status: Status) {
            }

            override fun onNotification(notification: Notification) {
                launch(UI){
                    itemList.insert(NotificationModel(notification), 0)
                    tootlist.adapter.notifyItemInserted(0)
                }
            }

            override fun onDelete(id: Long) {
            }

        }
        val streaming = MastodonStreamingTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken()).getStreaming()
        object : StreamingAsyncTask(){
            override fun doInBackground(vararg p0: Void?): String? {
                try{
                    shutdownable = streaming?.user(handler)
                }catch (e : Mastodon4jRequestException){
                    e.message?.showToastLogE(context)
                }
                return null
            }
        }.execute()
    }

    fun listScroll2Top(){
        tootlist.smoothScrollToPosition(0)
    }
}