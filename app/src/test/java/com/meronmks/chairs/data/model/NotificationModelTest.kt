package com.meronmks.chairs.data.model

import com.google.gson.Gson
import com.meronmks.chairs.BuildConfig
import com.meronmks.chairs.R
import com.meronmks.chairs.extensions.toIsoZonedDateTime
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileReader

/**
 * Created by meron on 2018/02/25.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [21])
class NotificationModelTest{
    lateinit var notificationModel : NotificationModel
    @Before
    fun setUp() {
        val file = File(NotificationModelTest::class.java.classLoader.getResource("notification.json").path)
        assertNotNull(file)
        val fileReader = FileReader(file)
        val strbuilder = StringBuilder()
        fileReader.forEachLine {
            strbuilder.append(it)
        }
        fileReader.close()
        notificationModel = NotificationModel(Gson().fromJson(strbuilder.toString(), Notification::class.java))
    }

    @Test
    fun トゥート時刻が3秒以内でnowとなるか(){
        val context = RuntimeEnvironment.application
        val time = "2018-02-22T16:08:02.150Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_now), notificationModel.createAt(context, time))
    }

    @Test
    fun トゥート時刻が3秒を越えたら秒表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2018-02-22T16:08:10.150Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_second, 10), notificationModel.createAt(context, time))
    }

    @Test
    fun トゥート時刻が60秒を越えたら分表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2018-02-22T16:09:00.150Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_min, 1), notificationModel.createAt(context, time))
    }

    @Test
    fun トゥート時刻が60分を越えたら時間表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2018-02-22T17:08:00.150Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_hour, 1), notificationModel.createAt(context, time))
    }

    @Test
    fun トゥート時刻が24時を越えたら日付表記となるか(){
        val context = RuntimeEnvironment.application
        val time = "2018-02-23T16:08:00.150Z".toIsoZonedDateTime().toInstant().toEpochMilli()
        assertEquals(context.getString(R.string.status_day, 1), notificationModel.createAt(context, time))
    }
}