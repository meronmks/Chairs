package com.meronmks.chairs.ViewPages.Adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.meronmks.chairs.ViewPages.Fragments.HomeFragment

/**
 * Created by meron on 2018/01/04.
 */
class HomeFragmentPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    private val PAGE_COUNT = 4
    private val tabTitles = arrayOf("Tab1", "Tab2", "Tab3", "Tab4")

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return HomeFragment()
            1 -> return HomeFragment()
            2 -> return HomeFragment()
            3 -> return HomeFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence {
        // Generate title based on item position
        return tabTitles[position]
    }
}