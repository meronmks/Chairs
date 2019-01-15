package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ArrayAdapter
import com.meronmks.chairs.Interfaces.ItemClickListener
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.TimeLineAdapter
import com.meronmks.chairs.ViewPages.HomeViewPage
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

/**
 * Created by meron on 2018/01/04.
 * 連合TLを表示するFragment
 */
class PublicTLFragment : BaseFragment(), ItemClickListener {
    override fun onItemClick(view: View, position: Int) {
        val item =  itemList.getItem(position)
        if(item.reblog == null) {
            (activity as HomeViewPage).showTootDtail(item.tootID, item.avater, item.content(), item.userName)
        }else{
            (activity as HomeViewPage).showTootDtail(item.reblog.tootID, item.reblog.avater, item.reblog.content(), item.reblog.userName)
        }
    }

    lateinit var timeLine : MastodonTimeLineTool
    lateinit var itemList: ArrayAdapter<TimeLineStatus>
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        timeLine = MastodonTimeLineTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter<TimeLineStatus>(context,0)
        tootList.adapter = TimeLineAdapter(context!!, this, itemList)
        tootList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        refreshPublicTimeLine()
        homeTootListRefresh.setOnRefreshListener {
            refreshPublicTimeLine(Range(sinceId = itemList.getItem(0).tootID))
        }
        tootList.addOnScrollListener(InfiniteScrollListener(homeTootList.layoutManager as LinearLayoutManager){
            refreshPublicTimeLine(Range(maxId = itemList.getItem(itemList.count - 1).tootID))
        })
        CreateStatusHandler(itemList, "Public")
    }

    private fun refreshPublicTimeLine(range: Range = Range()) = GlobalScope.launch(Dispatchers.Main){
        if(loadLock) return@launch
        loadLock = true
        homeTootListRefresh.isRefreshing = true
        val list = getTimeLine(range)
        list.forEach {
            itemList.add(TimeLineStatus(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootList.adapter?.notifyDataSetChanged()
        if(range.maxId == null) (tootList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(list.size, 0)
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    private suspend fun getTimeLine(range: Range = Range()): List<Status> {
        val list : ArrayList<Status> = arrayListOf()
        try{
            list.addAll(timeLine.getPublicTLAsync(range).await())
        }catch (timeoutE : SocketTimeoutException){
            timeoutE.localizedMessage.showToastLogE(context)
        }
        return list
    }
}