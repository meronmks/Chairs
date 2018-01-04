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
    }
}
