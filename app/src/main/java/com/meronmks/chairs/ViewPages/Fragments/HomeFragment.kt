package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.DataBaseTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.ViewPages.Adapter.List.HomeTimeLineAdapter
import com.meronmks.chairs.data.model.TimeLineStatus
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by meron on 2018/01/04.
 * ホームタイムラインを表示する奴
 */
class HomeFragment : Fragment() {

    lateinit var dataBase : DataBaseTool
    lateinit var timeLine : MastodonTimeLineTool
    lateinit var adapter : HomeTimeLineAdapter
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataBase = DataBaseTool(context)
        timeLine = MastodonTimeLineTool(dataBase.readInstanceName(), dataBase.readAccessToken())
        adapter = HomeTimeLineAdapter(context)
        homeTootList.adapter = adapter
        refresHomeTimeLine()
        homeTootListRefresh.setOnRefreshListener {
            refresHomeTimeLine(Range(sinceId = adapter.getItem(0).tootID))
        }
    }

    fun refresHomeTimeLine(range: Range = Range()) = launch(UI){
        homeTootListRefresh.isRefreshing = true
        val list = getTimeLine(range)
        list.forEach {
            adapter.add(TimeLineStatus(it))
        }
        adapter.sort { item1, item2 -> return@sort item2.tootCreateAt.compareTo(item1.tootCreateAt) }
        homeTootListRefresh.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_time_line, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBase.close()
    }

    suspend fun getTimeLine(range: Range = Range()): List<Status> {
        return timeLine.getHomeAsync(range).await()
    }
}