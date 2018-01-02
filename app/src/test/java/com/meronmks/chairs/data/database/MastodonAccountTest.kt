package com.meronmks.chairs.data.database

import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

/**
 * Created by meron on 2018/01/01.
 */
class MastodonAccountTest {

    lateinit var mMastodonAccount : MastodonAccount
    @Before
    fun setUp() {
        mMastodonAccount = MastodonAccount()
    }

    @Test
    fun getId() {
    }

    @Test
    fun setId() {
        mMastodonAccount.id = "123"
        assertNotNull(mMastodonAccount.id)
        assertEquals("123", mMastodonAccount.id)
    }

    @Test
    fun getInstanceName() {
    }

    @Test
    fun setInstanceName() {
    }

    @Test
    fun getUserName() {
    }

    @Test
    fun setUserName() {
    }

    @Test
    fun getAccessToken() {
    }

    @Test
    fun setAccessToken() {
    }

    @Test
    fun getLastLogin() {
    }

    @Test
    fun setLastLogin() {
    }

}