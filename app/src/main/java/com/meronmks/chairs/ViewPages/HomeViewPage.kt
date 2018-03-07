package com.meronmks.chairs.ViewPages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonTootTool
import com.meronmks.chairs.ViewPages.Adapter.HomeFragmentPagerAdapter
import com.meronmks.chairs.extensions.showToast
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import kotlinx.android.synthetic.main.activity_home_view_page.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.view.KeyEvent
import com.meronmks.chairs.ViewPages.Fragments.HomeFragment
import com.meronmks.chairs.ViewPages.Fragments.LocalPublicTLFragment
import com.meronmks.chairs.ViewPages.Fragments.NotificationFragment
import com.meronmks.chairs.ViewPages.Fragments.PublicTLFragment
import com.meronmks.chairs.extensions.showToastLogD


class HomeViewPage : AppCompatActivity() {

    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var tootTool : MastodonTootTool
    lateinit var adapter: HomeFragmentPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view_page)
        accountDataBase = AccountDataBaseTool(baseContext)
        tootTool = MastodonTootTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
        adapter = HomeFragmentPagerAdapter(supportFragmentManager)
        homeViewPager.adapter = adapter
        homeViewPager.offscreenPageLimit = (homeViewPager.adapter as HomeFragmentPagerAdapter).count - 1  //保持するページを全ページに
        homeTabs.setupWithViewPager(homeViewPager)
        homeTabs.getTabAt(0)?.setIcon(R.drawable.ic_home_black_24dp)
        homeTabs.getTabAt(1)?.setIcon(R.drawable.ic_notifications_black_24dp)
        homeTabs.getTabAt(2)?.setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
        homeTabs.getTabAt(3)?.setIcon(R.drawable.ic_people_black_24dp)
        homeTabs.getTabAt(4)?.setIcon(R.drawable.ic_public_black_24dp)
        homeTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0-> (adapter.getCurrentFrangemt() as HomeFragment).listScroll2Top()
                    1-> (adapter.getCurrentFrangemt() as NotificationFragment).listScroll2Top()
                    3-> (adapter.getCurrentFrangemt() as LocalPublicTLFragment).listScroll2Top()
                    4-> (adapter.getCurrentFrangemt() as PublicTLFragment).listScroll2Top()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
        sendTootImageButton.setOnClickListener {
            postToot()
        }
        textEditorKeyDown()
        textCounter()
    }

    override fun onPostResume() {
        tootCounterText.text = "${500 - tootEditText.text.length}"
        super.onPostResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase.close()
    }

    private fun postToot() = launch(UI){
        try {
            tootTool.tootAsync(tootEditText.text.toString(), null, null, false, null, Status.Visibility.Public).await()
            tootEditText.text.clear()
            getString(R.string.SuccessPostToot).showToast(baseContext, Toast.LENGTH_SHORT)
        }catch (e: Mastodon4jRequestException){
            "${getString(R.string.postFaild)} ${e.response?.code()}".showToastLogE(baseContext)
        }catch (e: Exception){
            e.message?.showToastLogE(baseContext)
        }
    }

    /**
     * Shift＋Enterの読み取り
     * エミュでは動作しない模様（別のショトカに取られてる？）
     */
    private fun textEditorKeyDown(){
        tootEditText.setOnKeyListener { view, keyCode, event ->
            if(event.isShiftPressed && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN){
                postToot()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    /**
     * 入力された文字のカウント処理
     */
    private fun textCounter(){
        tootEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                tootCounterText.text = "${500 - tootEditText.text.length}"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }
}
