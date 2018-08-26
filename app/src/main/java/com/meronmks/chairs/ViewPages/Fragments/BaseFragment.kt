package com.meronmks.chairs.ViewPages.Fragments

import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.meronmks.chairs.R
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.Tools.MastodonStreamingTool
import com.meronmks.chairs.data.model.NotificationModel
import com.meronmks.chairs.data.model.TimeLineStatus
import com.meronmks.chairs.extensions.StreamingAsyncTask
import com.meronmks.chairs.extensions.showToastLogE
import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import kotlinx.android.synthetic.main.fragment_home_time_line.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

open class BaseFragment : Fragment(){
    protected lateinit var accountDataBase: AccountDataBaseTool
    protected var loadLock : Boolean = false
    protected var shutdownable : Shutdownable? = null
    protected val tootList: RecyclerView by lazy { homeTootList }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_time_line, container, false)
    }

    protected fun CreateStatusHandler(itemList: ArrayAdapter<TimeLineStatus>, timeLineType: String){
        val handler = object : com.sys1yagi.mastodon4j.api.Handler{
            override fun onStatus(status: Status) {
                launch(UI){
                    itemList.insert(TimeLineStatus(status), 0)
                    tootList.adapter?.notifyItemInserted(0)
                    if(checkListPosTop()) {
                        listScrollToTop()
                    }
                }
            }

            override fun onNotification(notification: Notification) {
            }

            override fun onDelete(id: Long) {
                launch(UI){
                    for(i in itemList.count - 1 downTo 0){
                        if(id != itemList.getItem(i).tootID) continue
                        itemList.remove(itemList.getItem(i))
                        tootList.adapter?.notifyItemRemoved(i)
                    }
                }
            }
        }
        when (timeLineType) {
            "Home" -> createUserStreaming(handler)
            "LocalPublic" -> createLocalPublicStreaming(handler)
            "Public" -> createPublicStreaming(handler)
            "List" -> createUserListStreaming(handler)
        }
    }

    protected fun CreateNotificationHandler(itemList: ArrayAdapter<NotificationModel>){
        val handler = object : com.sys1yagi.mastodon4j.api.Handler{
            override fun onStatus(status: Status) {
            }

            override fun onNotification(notification: Notification) {
                launch(UI){
                    itemList.insert(NotificationModel(notification), 0)
                    tootList.adapter?.notifyItemInserted(0)
                    if(checkListPosTop()) {
                        tootList.scrollToPosition(0)
                    }
                }
            }

            override fun onDelete(id: Long) {
                launch(UI){
                    for(i in itemList.count - 1 downTo 0){
                        if(id != itemList.getItem(i).id) continue
                        itemList.remove(itemList.getItem(i))
                        tootList.adapter?.notifyItemRemoved(i)
                    }
                }
            }
        }
        createUserStreaming(handler)
    }

    private fun createUserListStreaming(handler: Handler){
        val streaming = MastodonStreamingTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken()).getStreaming()
        object : StreamingAsyncTask(){
            override fun doInBackground(vararg p0: Void?): String? {
                try{
                    shutdownable = streaming?.userList(handler)
                }catch (e : Mastodon4jRequestException){
                    showErrorToast(e)
                }
                return null
            }
        }.execute()
    }

    private fun createUserStreaming(handler: Handler){
        val streaming = MastodonStreamingTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken()).getStreaming()
        object : StreamingAsyncTask(){
            override fun doInBackground(vararg p0: Void?): String? {
                try{
                    shutdownable = streaming?.user(handler)
                }catch (e : Mastodon4jRequestException){
                    showErrorToast(e)
                }
                return null
            }
        }.execute()
    }

    private fun createLocalPublicStreaming(handler: Handler){
        val streaming = MastodonStreamingTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken()).getStreaming()
        object : StreamingAsyncTask(){
            override fun doInBackground(vararg p0: Void?): String? {
                try{
                    shutdownable = streaming?.localPublic(handler)
                }catch (e : Mastodon4jRequestException){
                    showErrorToast(e)
                }
                return null
            }
        }.execute()
    }

    private fun createPublicStreaming(handler: Handler){
        val streaming = MastodonStreamingTool(accountDataBase.readInstanceName(), accountDataBase.readAccessToken()).getStreaming()
        object : StreamingAsyncTask(){
            override fun doInBackground(vararg p0: Void?): String? {
                try{
                    shutdownable = streaming?.federatedPublic(handler)
                }catch (e : Mastodon4jRequestException){
                    showErrorToast(e)
                }
                return null
            }
        }.execute()
    }

    private fun showErrorToast(e : Mastodon4jRequestException){
        if(e.localizedMessage.isEmpty()){
            e.response.toString().showToastLogE(context)
        }else{
            e.localizedMessage.showToastLogE(context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase?.close()
        shutdownable?.shutdown()
    }

    protected fun listScrollToTop(){
        tootList.scrollToPosition(0)
    }

    protected fun checkListPosTop(): Boolean {
        return ((tootList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0)
    }

    fun listScroll2Top(){
        tootList.smoothScrollToPosition(0)
    }
}