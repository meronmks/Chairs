package com.meronmks.chairs.ViewPages.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.meronmks.chairs.ViewPages.Fragments.*

/**
 * Created by meron on 2018/01/04.
 */
open class HomeFragmentPagerAdapter(fm : androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    private val PAGE_COUNT = 5
    private var currentFragment: androidx.fragment.app.Fragment? = null
    private var position: Int = 0

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return HomeFragment()
            1 -> return NotificationFragment()
            2 -> return ListTLFragment()
            3 -> return LocalPublicTLFragment()
            4 -> return PublicTLFragment()
        }
        return DummyFragment()
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
            currentFragment = `object` as androidx.fragment.app.Fragment
        }
        this.position = position
        super.setPrimaryItem(container, position, `object`)
    }

    open fun getCurrentFrangemt(): androidx.fragment.app.Fragment {
        return currentFragment!!
    }
}