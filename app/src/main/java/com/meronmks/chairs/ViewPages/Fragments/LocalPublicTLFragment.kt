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
import com.meronmks.chairs.Tools.MastodonStreamingTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.TimeLineAdapter
import com.meronmks.chairs.ViewPages.ViewHolder.TimeLineViewHolder
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.StreamingAsyncTask
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by meron on 2018/01/04.
 */
class LocalPublicTLFragment : Fragment(), TimeLineViewHolder.ItemClickListener {
    override fun onItemClick(view: View, position: Int) {

    }

    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var timeLine : MastodonTimeLineTool
    var loadLock : Boolean = false
    var shutdownable : Shutdownable? = null
    lateinit var itemList: ArrayAdapter<TimeLineStatus>
    private val tootlist by lazy { homeTootList }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        timeLine = MastodonTimeLineTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<TimeLineStatus>(context,0)
        tootlist.adapter = TimeLineAdapter(context!!, this, itemList)
        tootlist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refreshLocalPublicTimeLine()
        homeTootListRefresh.setOnRefreshListener {
            refreshLocalPublicTimeLine(Range(sinceId = itemList.getItem(0).tootID))
        }
        tootlist.addOnScrollListener(InfiniteScrollListener(homeTootList.layoutManager as LinearLayoutManager){
            refreshLocalPublicTimeLine(Range(maxId = itemList.getItem(itemList.count - 1).tootID))
        })
        CreateHandler()
    }

    fun refreshLocalPublicTimeLine(range: Range = Range()) = launch(UI) {
        if (loadLock) return@launch
        loadLock = true
        homeTootListRefresh.isRefreshing = true
        val list = getTimeLine(range)
        list.forEach {
            itemList.add(TimeLineStatus(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootlist.adapter.notifyDataSetChanged()
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    fun CreateHandler(){
        val handler = object : com.sys1yagi.mastodon4j.api.Handler{
            override fun onStatus(status: Status) {
                launch(UI) {
                    itemList.insert(TimeLineStatus(status), 0)
                    tootlist.adapter.notifyItemInserted(0)
                }
            }

            override fun onNotification(notification: Notification) {
            }

            override fun onDelete(id: Long) {
            }

        }
        val streaming = MastodonStreamingTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken()).getStreaming()
        try{
            object : StreamingAsyncTask(){
                override fun doInBackground(vararg p0: Void?): String? {
                    shutdownable = streaming?.localPublic(handler)
                    return null
                }
            }.execute()
        }catch (e : Exception){
            e.message?.showToastLogE(context)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_time_line, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase?.close()
        shutdownable?.shutdown()
    }

    suspend fun getTimeLine(range: Range = Range()): List<Status> {
        return timeLine.getLocalPublicTLAsync(range).await()
    }

    fun listScroll2Top(){
        tootlist.smoothScrollToPosition(0)
    }
}