package com.meronmks.chairs

import android.util.Log
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

/**
 * Created by meron on 2018/01/03.
 */
class SetInstanceNameActivityTest {
    @Test
    fun addInstance() {
        val setInstanceNameActivity = SetInstanceNameActivity()
        val method = setInstanceNameActivity::class.java.getDeclaredMethod("registerApp", String::class.java)
        method.isAccessible = true
        val appRegistration : AppRegistration = method.invoke(SetInstanceNameActivity(), "mstdn.jp") as AppRegistration
        assertNotNull(appRegistration.clientId)
    }

}