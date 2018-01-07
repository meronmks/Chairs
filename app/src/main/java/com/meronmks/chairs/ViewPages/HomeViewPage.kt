package com.meronmks.chairs.ViewPages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.meronmks.chairs.R
import com.meronmks.chairs.ViewPages.Adapter.HomeFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_home_view_page.*

class HomeViewPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view_page)
        homeViewPager.adapter = HomeFragmentPagerAdapter(supportFragmentManager)
        homeTabs.setupWithViewPager(homeViewPager)
        homeTabs.getTabAt(0)?.setIcon(R.drawable.ic_home_black_24dp)
        homeTabs.getTabAt(1)?.setIcon(R.drawable.ic_notifications_black_24dp)
        homeTabs.getTabAt(2)?.setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
        homeTabs.getTabAt(3)?.setIcon(R.drawable.ic_people_black_24dp)
        homeTabs.getTabAt(4)?.setIcon(R.drawable.ic_public_black_24dp)
    }
}
