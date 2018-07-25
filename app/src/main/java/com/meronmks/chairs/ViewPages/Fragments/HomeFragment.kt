package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.data.model.TimeLineStatus
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.widget.ArrayAdapter
import com.meronmks.chairs.Tools.MastodonStreamingTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.TimeLineAdapter
import com.meronmks.chairs.ViewPages.HomeViewPage
import com.meronmks.chairs.Interfaces.ItemClickListener
import com.meronmks.chairs.extensions.StreamingAsyncTask
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException


/**
 * Created by meron on 2018/01/04.
 * ホームタイムラインを表示する奴
 */
class HomeFragment : Fragment(), ItemClickListener {
    override fun onItemClick(view: View, position: Int) {
        val item =  itemList.getItem(position)
        if(item.reblog == null) {
            (activity as HomeViewPage).showTootDtail(item.tootID, item.avater, item.content(), item.userName)
        }else{
            (activity as HomeViewPage).showTootDtail(item.reblog.tootID, item.reblog.avater, item.reblog.content(), item.reblog.userName)
        }
    }

    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var timeLine : MastodonTimeLineTool
    var loadLock : Boolean = false
    var shutdownable : Shutdownable? = null
    lateinit var itemList: ArrayAdapter<TimeLineStatus>
    private val tootList by lazy { homeTootList }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        timeLine = MastodonTimeLineTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<TimeLineStatus>(context,0)
        tootList.adapter = TimeLineAdapter(context!!, this, itemList)
        tootList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refreshHomeTimeLine()
        homeTootListRefresh.setOnRefreshListener {
            refreshHomeTimeLine(Range(sinceId = itemList.getItem(0).tootID))
        }

        tootList.addOnScrollListener(InfiniteScrollListener(tootList.layoutManager as LinearLayoutManager){
            refreshHomeTimeLine(Range(maxId = itemList.getItem(itemList.count - 1).tootID))
        })
        CreateHandler()
    }

    fun refreshHomeTimeLine(range: Range = Range()) = launch(UI){
        if(loadLock) return@launch
        loadLock = true
        homeTootListRefresh.isRefreshing = true
        val list = getTimeLine(range)
        list.forEach {
            itemList.add(TimeLineStatus(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootList.adapter.notifyDataSetChanged()
        (tootList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(list.size, 0)
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    fun CreateHandler(){
        val handler = object : com.sys1yagi.mastodon4j.api.Handler{
            override fun onStatus(status: Status) {
                launch(UI){
                    itemList.insert(TimeLineStatus(status), 0)
                    tootList.adapter?.notifyItemInserted(0)
                    if(chackListPosTop()) {
                        tootList.scrollToPosition(0)
                    }
                }
            }

            override fun onNotification(notification: Notification) {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_time_line, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase?.close()
        shutdownable?.shutdown()
    }

    suspend fun getTimeLine(range: Range = Range()): List<Status> {
        return timeLine.getHomeAsync(range).await()
    }

    private fun chackListPosTop(): Boolean {
        return ((tootList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0)
    }

    fun listScroll2Top(){
        tootList.smoothScrollToPosition(0)
    }
}