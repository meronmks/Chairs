package com.meronmks.chairs.ViewPages

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.design.widget.TabLayout
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonHomeViewTools
import com.meronmks.chairs.ViewPages.Adapter.HomeFragmentPagerAdapter
import com.meronmks.chairs.extensions.showToast
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import kotlinx.android.synthetic.main.activity_home_view_page.*
import kotlinx.coroutines.experimental.launch
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.meronmks.chairs.Settings.MainPreferenceFragment
import com.meronmks.chairs.ViewPages.Fragments.*
import com.meronmks.chairs.extensions.fromHtml
import com.sys1yagi.mastodon4j.api.entity.Attachment
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


class HomeViewPage : AppCompatActivity(), TextWatcher {

    lateinit var accountDataBase: AccountDataBaseTool
    lateinit var homeViewTools : MastodonHomeViewTools
    lateinit var adapter: HomeFragmentPagerAdapter
    var isSensitive: Boolean = false
    var isCW: Boolean = false
    var statusID : Long = 0
    var lock: Boolean = false
    var userName : String? = null
    private val RESULT_PICK_IMAGEFILE: Int = 1000
    private val medias = arrayListOf<Attachment>()
    private val postImages = arrayListOf<ImageButton>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view_page)
        supportActionBar?.hide()
        accountDataBase = AccountDataBaseTool(baseContext)
        homeViewTools = MastodonHomeViewTools(accountDataBase.readInstanceName(), accountDataBase.readAccessToken())
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
                    2-> (adapter.getCurrentFrangemt() as ListTLFragment).listScroll2Top()
                    3-> (adapter.getCurrentFrangemt() as LocalPublicTLFragment).listScroll2Top()
                    4-> (adapter.getCurrentFrangemt() as PublicTLFragment).listScroll2Top()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })

        iniLayout()

        sendTootImageButton.setOnClickListener {
            postToot()
        }

        mainMenuButtons()
        textEditorKeyDown()
        textCounter()
        tootDtailButton()
    }

    override fun onPostResume() {
        tootCounterText.text = "${500 - tootEditText.text.length}"
        super.onPostResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase?.close()
    }

    fun showTootDtail(statusID : Long, avater : String, content : String?, userName : String?){
        tootDetail.visibility = View.VISIBLE
        Glide.with(applicationContext).load(avater).into(detailAvatarImageButton)
        detailTootTextView.text = content?.fromHtml(baseContext, detailTootTextView)
        this.statusID = statusID
        this.userName = userName
    }

    /**
     * メニューに関するボタン類の処理
     */
    private fun mainMenuButtons(){
        menuButton.setOnClickListener {
            if(linearMenu.visibility == View.VISIBLE){
                linearMenu.visibility = View.GONE
                menuButton.setImageResource(R.drawable.menu_open_black_24dp)
            }else if (linearMenu.visibility == View.GONE){
                linearMenu.visibility = View.VISIBLE
                menuButton.setImageResource(R.drawable.menu_close_black_24dp)
            }
        }

        settingsButton.setOnClickListener {
            val intent = Intent(baseContext, MainPreferenceFragment::class.java)
            startActivity(intent)
        }

        uploadMediaButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
        }

        nsfwButton.setOnClickListener {
            isSensitive = !isSensitive
            buttonTextColorChange(nsfwButton, isSensitive)
        }

        cwButton.setOnClickListener {
            isCW = !isCW
            buttonTextColorChange(cwButton, isCW)
            linearCWTextLayout.visibility = View.VISIBLE
            if(!isCW){
                linearCWTextLayout.visibility = View.GONE
                cwEditText.text.clear()
            }
        }
    }

    /**
     * トゥート詳細のボタン設定
     **/
    private fun tootDtailButton(){
        detailCloseButton.setOnClickListener {
            tootDetail.visibility = View.GONE
            userName = null
        }
        replayButton.setOnClickListener {
            tootEditText.requestFocus()
            tootEditText.setText("@$userName ${tootEditText.text}")
            tootEditText.setSelection(tootEditText.text.length)
        }
        reblogButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    homeViewTools.postAsyncReblog(statusID).await()
                    getString(R.string.SuccessReblog).showToast(baseContext, Toast.LENGTH_SHORT)
                }catch (e: Mastodon4jRequestException){
                    "${getString(R.string.reblogFaild)} ${e.response?.code()}".showToastLogE(baseContext)
                }
            }
        }
        favButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    homeViewTools.postAsyncFavourite(statusID).await()
                    getString(R.string.fovouriteSuccess).showToast(baseContext, Toast.LENGTH_SHORT)
                }catch (e: Mastodon4jRequestException){
                    "${getString(R.string.favouriteFaild)} ${e.response?.code()}".showToastLogE(baseContext)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fun getMetaData(context: Context, uri: Uri): Pair<String, String> {
            val cursor = context.contentResolver
                    .query(uri, null, null, null, null, null)
            try {
                return cursor?.let {
                    it.moveToFirst()
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val name = it.getString(nameIndex)
                    val mimeTypeIndex = cursor.getColumnIndex("mime_type")
                    val mimeType = it.getString(mimeTypeIndex)

                    Pair(name, mimeType)
                } ?: throw IllegalStateException("cursor not found $uri") as Throwable
            } finally {
                cursor?.close()
            }
        }
        if(requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK){
            val uri: Uri? = data?.data
            if (uri != null){
                val (name, mimeType) = getMetaData(baseContext, uri)
                val bytes = baseContext.contentResolver.openInputStream(uri).readBytes()
                val requestBody = RequestBody.create(MediaType.parse(mimeType), bytes)
                val multipart: MultipartBody.Part = MultipartBody.Part.createFormData("file", "Chairs_$name", requestBody)
                uploadMedia(multipart)
            }
        }
    }

    fun uploadMedia(multipart: MultipartBody.Part) = GlobalScope.launch(Dispatchers.Main) {
        val attachment = homeViewTools.uploadMedia(multipart).await()
        if(attachment != null){
            medias.add(attachment)
        }
        var i = 0
        val options = RequestOptions()
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_error_24dp)
        medias.forEach {
            postImages[i].visibility = View.VISIBLE
            Glide.with(baseContext)
                    .load(it.previewUrl)
                    .apply(options)
                    .into(postImages[i])
            i++
        }
    }

    /**
     * トゥートを送信する
     **/
    private fun postToot() = GlobalScope.launch(Dispatchers.Main){
        try {
            if(lock) return@launch
            lock = true
            sendTootProgressBar.visibility = View.VISIBLE
            var replayID : Long? = statusID
            if (userName == null) replayID = null
            val mediaIDs:  ArrayList<Long> = ArrayList()
            medias.forEach {
                mediaIDs.add(it.id)
            }
            homeViewTools.tootAsync(tootEditText.text.toString(), replayID, mediaIDs, isSensitive, cwEditText.text.toString(), Status.Visibility.Public).await()
            tootEditText.text.clear()
            cwEditText.text.clear()
            getString(R.string.SuccessPostToot).showToast(baseContext, Toast.LENGTH_SHORT)
        }catch (e: Mastodon4jRequestException){
            "${getString(R.string.postFaild)} ${e.response?.code()}".showToastLogE(baseContext)
        }catch (e: Exception){
            e.message?.showToastLogE(baseContext)
        }finally {
            lock = false
            sendTootProgressBar.visibility = View.GONE
        }
    }

    /**
     * Shift＋Enterの読み取り
     * エミュでは動作しない模様
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
        tootEditText.addTextChangedListener(this)

        cwEditText.addTextChangedListener(this)
    }

    /**
     * Layoutの初期化
     */
    private fun iniLayout(){
        linearMenu.visibility = View.GONE
        tootDetail.visibility = View.GONE
        linearCWTextLayout.visibility = View.GONE
        buttonTextColorChange(nsfwButton, isSensitive)
        buttonTextColorChange(cwButton, isCW)
        sendTootProgressBar.visibility = View.GONE
        postImage1.visibility = View.GONE
        postImage2.visibility = View.GONE
        postImage3.visibility = View.GONE
        postImage4.visibility = View.GONE
        postImages.add(postImage1)
        postImages.add(postImage2)
        postImages.add(postImage3)
        postImages.add(postImage4)
    }

    private fun buttonTextColorChange(b: Button, flag: Boolean){
        if(flag){
            b.setTextColor(Color.CYAN)
        }else{
            b.setTextColor(Color.WHITE)
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        tootCounterText.text = "${500 - (tootEditText.text.length + cwEditText.text.length)}"
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}
