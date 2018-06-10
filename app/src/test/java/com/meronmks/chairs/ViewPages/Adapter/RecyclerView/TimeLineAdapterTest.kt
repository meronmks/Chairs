package com.meronmks.chairs.ViewPages.Adapter.RecyclerView

import android.view.LayoutInflater
import android.view.View
import com.google.gson.Gson
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import com.meronmks.chairs.ViewPages.ViewHolder.TimeLineViewHolder
import com.meronmks.chairs.data.model.NotificationModel
import com.meronmks.chairs.data.model.NotificationModelTest
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.data.model.TimeLineStatusTest
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileReader
import java.sql.Time
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment





/**
 * Created by meron on 2018/06/10.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [21])
class TimeLineAdapterTest{

    lateinit var holder: TimeLineViewHolder
    lateinit var item : TimeLineStatus
    lateinit var adapter: TimeLineAdapter

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.application
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.toot_item, null, false)
        holder = TimeLineViewHolder(mView)
        val file = File(TimeLineStatusTest::class.java.classLoader.getResource("status.json").path)
        assertNotNull(file)
        val fileReader = FileReader(file)
        val strbuilder = StringBuilder()
        fileReader.forEachLine {
            strbuilder.append(it)
        }
        fileReader.close()
        item = TimeLineStatus(Gson().fromJson(strbuilder.toString(), Status::class.java))
        adapter = TimeLineAdapter(context, null, null)
    }

    @Test
    fun isSensitiveがTrueなら画像非表示用レイアウトが出るか(){
        adapter.changeNSFWMedia(holder, true)
        assertEquals(View.VISIBLE, holder.sensitiveText.visibility)
    }

    @Test
    fun isSensitiveがFalseなら画像非表示用レイアウトが消えるか(){
        adapter.changeNSFWMedia(holder, false)
        assertEquals(View.GONE, holder.sensitiveText.visibility)
    }
}