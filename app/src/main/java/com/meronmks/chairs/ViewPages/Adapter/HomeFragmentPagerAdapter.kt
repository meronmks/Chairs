package com.meronmks.chairs.ViewPages.Adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.meronmks.chairs.ViewPages.Fragments.*

/**
 * Created by meron on 2018/01/04.
 */
class HomeFragmentPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    private val PAGE_COUNT = 5

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return HomeFragment()
            1 -> return NotificationFragment()
            2 -> return DummyFragment()
            3 -> return LocalPublicTLFragment()
            4 -> return PublicTLFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return null
    }
}