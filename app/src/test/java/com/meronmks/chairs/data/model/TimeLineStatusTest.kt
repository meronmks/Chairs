package com.meronmks.chairs.data.model

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.api.entity.Status
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before

import org.junit.Assert.*
import org.json.JSONObject
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader


/**
 * Created by meron on 2018/01/04.
 *
 */
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
    fun トゥート本文がリソースと正しいか() {
        assertEquals("<p>やばい。トイレがお友達状態</p>", timeLineStatus.content())
    }

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
}