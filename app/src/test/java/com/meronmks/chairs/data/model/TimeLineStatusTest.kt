package com.meronmks.chairs.data.model

import com.google.gson.Gson
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import com.meronmks.chairs.extensions.toIsoZonedDateTime
import com.sys1yagi.mastodon4j.api.entity.Status
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileReader


/**
 * Created by meron on 2018/01/04.
 *
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [21])
class TimeLineStatusTest {

    lateinit var timeLineStatus : TimeLineStatus
    @Before
    fun setUp() {
        val file = File(TimeLineStatusTest::class.java.classLoader.getResource("status.json").path)
        assertNotNull(file)
        val fileReader = FileReader(file)
        val strbuilder = StringBuilder()
        fileReader.forEachLine {
            strbuilder.append(it)
        }
        fileReader.close()
        timeLineStatus = TimeLineStatus(Gson().fromJson(strbuilder.toString(), Status::class.java))
    }

    @Test
    fun トゥート時刻が3秒以内でnowとなるか(){
        val context = RuntimeEnvironment.application
        val time = "2017-04-28T04:21:25.560Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_now), timeLineStatus.createAt(context, time))
    }

    @Test
    fun トゥート時刻が3秒を越えたら秒表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2017-04-28T04:21:35.560Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_second, 10), timeLineStatus.createAt(context, time))
    }

    @Test
    fun トゥート時刻が60秒を越えたら分表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2017-04-28T04:22:25.560Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_min, 1), timeLineStatus.createAt(context, time))
    }

    @Test
    fun トゥート時刻が60分を越えたら時間表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2017-04-28T05:21:25.560Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_hour, 1), timeLineStatus.createAt(context, time))
    }

    @Test
    fun トゥート時刻が24時を越えたら日付表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2017-04-29T04:21:25.560Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_day, 1), timeLineStatus.createAt(context, time))
    }

//    @Test
//    fun トゥート本文が意図したものか() {
//        assertEquals("やばい。トイレがお友達状態", timeLineStatus.content())
//    }

    @Test
    fun お気に入り済みにしたときに正しい数になっているか(){
        val expected = timeLineStatus.favouritedCount() + 1
        timeLineStatus.isFavourited = true
        assertEquals(expected, timeLineStatus.favouritedCount())
    }

    @Test
    fun ブースト済みにしたときに正しい数になっているか(){
        val expected = timeLineStatus.rebloggedCount() + 1
        timeLineStatus.isReblogged = true
        assertEquals(expected, timeLineStatus.rebloggedCount())
    }

    @Test
    fun 画像が存在するか正しく判定できるか(){
        assertEquals(false, timeLineStatus.isMediaAttach)
    }
}