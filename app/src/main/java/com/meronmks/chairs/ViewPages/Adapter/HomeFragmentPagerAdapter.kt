package com.meronmks.chairs.ViewPages.Adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.meronmks.chairs.ViewPages.Fragments.*

/**
 * Created by meron on 2018/01/04.
 */
open class HomeFragmentPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    private val PAGE_COUNT = 5
    private var currentFragment: Fragment? = null
    private var position: Int = 0

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return HomeFragment()
            1 -> return NotificationFragment()
            2 -> return ListTLFragment()
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

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if(currentFragment != `object`){
            currentFragment = `object` as Fragment
        }
        this.position = position
        super.setPrimaryItem(container, position, `object`)
    }

    open fun getCurrentFrangemt(): Fragment {
        return currentFragment!!
    }
}