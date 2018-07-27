package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonNotificationTool
import com.meronmks.chairs.Tools.MastodonStreamingTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.NotificationAdapter
import com.meronmks.chairs.ViewPages.HomeViewPage
import com.meronmks.chairs.Interfaces.ItemClickListener
import com.meronmks.chairs.R.id.homeTootList
import com.meronmks.chairs.data.model.NotificationModel
import com.meronmks.chairs.extensions.StreamingAsyncTask
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by meron on 2018/01/04.
 * 通知一覧を表示する奴
 */
class NotificationFragment : Fragment(), ItemClickListener {
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
    private val tootList by lazy { homeTootList }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        notification = MastodonNotificationTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<NotificationModel>(context,0)
        tootList.adapter = NotificationAdapter(context!!, this, itemList)
        tootList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refresNotification()
        homeTootListRefresh.setOnRefreshListener {
            refresNotification(Range(sinceId = itemList.getItem(0).id))
        }
        tootList.addOnScrollListener(InfiniteScrollListener(homeTootList.layoutManager as LinearLayoutManager){
            refresNotification(Range(maxId = itemList.getItem(itemList.count - 1).id))
        })
        CreateHandler()
    }

    fun refresNotification(range: Range = Range()) = launch(UI){
        if(loadLock)return@launch
        loadLock = true
        homeTootListRefresh.isRefreshing = true
        val list = getNotification(range)
        list.forEach {
            itemList.add(NotificationModel(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootList.adapter.notifyDataSetChanged()
        (tootList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(list.size, 0)
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_time_line, container, false)
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
                    tootList.adapter?.notifyItemInserted(0)
                    if(chackListPosTop()) {
                        tootList.scrollToPosition(0)
                    }
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

    private fun chackListPosTop(): Boolean {
        return ((tootList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0)
    }

    fun listScroll2Top(){
        tootList.smoothScrollToPosition(0)
    }
}