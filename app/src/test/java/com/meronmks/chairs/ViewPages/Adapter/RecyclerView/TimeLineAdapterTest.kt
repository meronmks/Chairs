package com.meronmks.chairs.ViewPages.Adapter.RecyclerView

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import com.meronmks.chairs.ViewPages.ViewHolders.TimeLineViewHolder
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.data.model.TimeLineStatusTest
import com.sys1yagi.mastodon4j.api.entity.Status
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileReader


/**
 * Created by meron on 2018/06/10.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [21])
class TimeLineAdapterTest{

    lateinit var holder: TimeLineViewHolder
    lateinit var item : TimeLineStatus
    lateinit var adapter: TimeLineAdapter
    lateinit var  context: Application
    var itemList:  ArrayAdapter<TimeLineStatus>? = null

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
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

    @Test
    fun itemListがnullで意図した値が帰ってくるか(){
        assertEquals(-1, adapter.itemCount)
    }

    @Test
    fun itemListが存在していて意図した値が返ってくるか(){
        itemList = ArrayAdapter(context,0)
        itemList?.add(item)
        adapter = TimeLineAdapter(context, null, itemList)
        assertEquals(1, adapter.itemCount)
    }
}