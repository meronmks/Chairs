package com.meronmks.chairs.ViewPages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.DataBaseTool
import com.meronmks.chairs.Tools.MastodonTootTool
import com.meronmks.chairs.ViewPages.Adapter.HomeFragmentPagerAdapter
import com.meronmks.chairs.extensions.showToastandLogD
import com.meronmks.chairs.extensions.showToastandLogE
import com.sys1yagi.mastodon4j.api.entity.Status
import kotlinx.android.synthetic.main.activity_home_view_page.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class HomeViewPage : AppCompatActivity() {

    lateinit var dataBase : DataBaseTool
    lateinit var tootTool : MastodonTootTool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view_page)
        dataBase = DataBaseTool(baseContext)
        tootTool = MastodonTootTool(dataBase.readInstanceName(), dataBase.readAccessToken())
        homeViewPager.adapter = HomeFragmentPagerAdapter(supportFragmentManager)
        homeTabs.setupWithViewPager(homeViewPager)
        homeTabs.getTabAt(0)?.setIcon(R.drawable.ic_home_black_24dp)
        homeTabs.getTabAt(1)?.setIcon(R.drawable.ic_notifications_black_24dp)
        homeTabs.getTabAt(2)?.setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
        homeTabs.getTabAt(3)?.setIcon(R.drawable.ic_people_black_24dp)
        homeTabs.getTabAt(4)?.setIcon(R.drawable.ic_public_black_24dp)
        sendTootImageButton.setOnClickListener {
            try {
                val status = tootTool.tootAsync(tootEditText.text.toString(), null, null, false, null, Status.Visibility.Public)
                tootEditText.text.clear()
                getString(R.string.SuccessPostToot).showToastandLogD(baseContext)
            }catch (e: Exception){
                e.message?.showToastandLogE(baseContext)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBase.close()
    }
}
