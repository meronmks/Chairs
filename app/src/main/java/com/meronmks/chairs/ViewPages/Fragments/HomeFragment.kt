package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.DataBaseTool
import com.meronmks.chairs.Tools.MastodonTimeLineTool
import com.meronmks.chairs.ViewPages.Adapter.HomeTimeLineAdapter
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
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataBase = DataBaseTool(context)
        timeLine = MastodonTimeLineTool(dataBase.readInstanceName(), dataBase.readAccessToken())
        val adapter = HomeTimeLineAdapter(context)
        homeTootList.adapter = adapter
        launch(UI){
            val list = getTimeLine()
            list.forEach {
                adapter.add(TimeLineStatus(it))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_time_line, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBase.close()
    }

    suspend fun getTimeLine(): List<Status> {
        return timeLine.getHomeAsync(Range()).await()
    }
}