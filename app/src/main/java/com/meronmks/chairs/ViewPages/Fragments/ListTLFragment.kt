package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.meronmks.chairs.Interfaces.ItemClickListener
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.InfiniteScrollListener
import com.meronmks.chairs.ViewPages.Adapter.RecyclerView.TimeLineAdapter
import com.meronmks.chairs.ViewPages.HomeViewPage
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.MastodonList
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import kotlinx.android.synthetic.main.fragment_list_time_line.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class ListTLFragment : BaseFragment(), ItemClickListener {
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
    lateinit var listsList: ArrayAdapter<MastodonList>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_time_line, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        accountDataBase = AccountDataBaseTool(context)
        timeLine = MastodonTimeLineTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        itemList = ArrayAdapter(context,0)
        listsList = ArrayAdapter(context, 0)
        tootList.adapter = TimeLineAdapter(context!!, this, itemList)
        tootList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        getLists()
        homeTootListRefresh.setOnRefreshListener {
            if(listsList.isEmpty) return@setOnRefreshListener
            if(itemList.isEmpty){
                refreshListTimeLine(listsList.getItem(listSpinner.selectedItemPosition).id)
            }else{
                refreshListTimeLine(listsList.getItem(listSpinner.selectedItemPosition).id, Range(sinceId = itemList.getItem(0).tootID))
            }

        }

        tootList.addOnScrollListener(InfiniteScrollListener(tootList.layoutManager as androidx.recyclerview.widget.LinearLayoutManager){
            if(listsList.isEmpty) return@InfiniteScrollListener
            refreshListTimeLine(listsList.getItem(listSpinner.selectedItemPosition).id, Range(maxId = itemList.getItem(itemList.count - 1).tootID))
        })

        listListReloadImageView.setOnClickListener {
            getLists()
        }
    }

    private fun refreshListTimeLine(listID: Long, range: Range = Range()) = GlobalScope.launch(Dispatchers.Main) {
        if(loadLock) return@launch
        loadLock = true
        CreateStatusHandler(itemList, "List", listID.toString())
        homeTootListRefresh.isRefreshing = true
        val toots = getListTimeLine(listID, range)
        toots.forEach {
            itemList.add(TimeLineStatus(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootList.adapter?.notifyDataSetChanged()
        if(range.maxId == null) (tootList.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(toots.size, 0)
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    private suspend fun getListTimeLine(listID: Long, range: Range): List<Status> {
        val list : ArrayList<Status> = arrayListOf()
        try{
            list.addAll(timeLine.getListTLAsync(listID, range).await())
        }catch (timeoutE : SocketTimeoutException){
            timeoutE.localizedMessage.showToastLogE(context)
        }catch (requestException : Mastodon4jRequestException)
        {
            requestException.localizedMessage.showToastLogE(context)
        }
        return list
    }

    private fun getLists() = GlobalScope.launch(Dispatchers.Main) {
        val lists : ArrayList<MastodonList> = arrayListOf()
        try{
            lists.addAll(timeLine.getListsAsync().await())
        }catch (timeoutE : SocketTimeoutException){
            timeoutE.localizedMessage.showToastLogE(context)
        }catch (requestException : Mastodon4jRequestException)
        {
            requestException.localizedMessage.showToastLogE(context)
        }
        val adapter = ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listSpinner.adapter = adapter
        adapter.clear()
        listsList.clear()
        lists.forEach {
            adapter.add(it.title)
            listsList.add(it)
        }
    }
}