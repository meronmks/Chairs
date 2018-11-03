package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.MastodonList
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.fragment_list_time_line.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

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
        tootList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        getLists()
        homeTootListRefresh.setOnRefreshListener {
            if(listsList.isEmpty) return@setOnRefreshListener
            if(itemList.isEmpty){
                getListTimeLine(listsList.getItem(listSpinner.selectedItemPosition).id)
            }else{
                getListTimeLine(listsList.getItem(listSpinner.selectedItemPosition).id, Range(sinceId = itemList.getItem(0).tootID))
            }

        }

        tootList.addOnScrollListener(InfiniteScrollListener(tootList.layoutManager as LinearLayoutManager){
            if(listsList.isEmpty) return@InfiniteScrollListener
            getListTimeLine(listsList.getItem(listSpinner.selectedItemPosition).id, Range(maxId = itemList.getItem(itemList.count - 1).tootID), true)
        })

        listListReloadImageView.setOnClickListener {
            getLists()
        }
    }

    private fun getListTimeLine(listID: Long, range: Range = Range(), nextFlag: Boolean = false) = launch(UI) {
        if(loadLock) return@launch
        loadLock = true
        CreateStatusHandler(itemList, "List", listID.toString())
        homeTootListRefresh.isRefreshing = true
        val toots = timeLine.getListTLAsync(listID, range).await()
        toots.forEach {
            itemList.add(TimeLineStatus(it))
        }
        itemList.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        tootList.adapter?.notifyDataSetChanged()
        if(!nextFlag) (tootList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(toots.size, 0)
        homeTootListRefresh.isRefreshing = false
        loadLock = false
    }

    private fun getLists() = launch(UI) {
        val lists = timeLine.getListsAsync().await()
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