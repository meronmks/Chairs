package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.ViewPages.Adapter.List.HomeTimeLineAdapter
import com.meronmks.chairs.data.model.TimeLineStatus
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.widget.AbsListView
import com.meronmks.chairs.Tools.MastodonStreamingTool
import com.meronmks.chairs.extensions.StreamingAsyncTask
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification


/**
 * Created by meron on 2018/01/04.
 * ホームタイムラインを表示する奴
 */
class HomeFragment : Fragment() {

    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var timeLine : MastodonTimeLineTool
    lateinit var adapter : HomeTimeLineAdapter
    var loadLock : Boolean = false
    lateinit var shutdownable : Shutdownable
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        timeLine = MastodonTimeLineTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        adapter = HomeTimeLineAdapter(context)
        homeTootList.adapter = adapter
        refreshHomeTimeLine()
        homeTootListRefresh.setOnRefreshListener {
            refreshHomeTimeLine(Range(sinceId = adapter.getItem(0).tootID))
        }
        homeTootList.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                if(homeTootList.lastVisiblePosition == adapter.count - 1){
                    refreshHomeTimeLine(Range(maxId = adapter.getItem(adapter.count - 1).tootID))
                }
            }

            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

            }

        })
        CreateHandler()
    }

    fun refreshHomeTimeLine(range: Range = Range()) = launch(UI){
        if(loadLock) return@launch
        loadLock = true
        homeTootListRefresh.isRefreshing = true
        val list = getTimeLine(range)
        list.forEach {
            adapter.add(TimeLineStatus(it))
        }
        adapter.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    fun CreateHandler(){
        val handler = object : com.sys1yagi.mastodon4j.api.Handler{
            override fun onStatus(status: Status) {
                launch(UI){
                    adapter.insert(TimeLineStatus(status), 0)
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
                    shutdownable = streaming.user(handler)
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
        accountDataBase.close()
        shutdownable.shutdown()
    }

    suspend fun getTimeLine(range: Range = Range()): List<Status> {
        return timeLine.getHomeAsync(range).await()
    }
}