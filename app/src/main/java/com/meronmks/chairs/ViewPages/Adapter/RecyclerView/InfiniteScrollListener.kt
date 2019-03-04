package com.meronmks.chairs.ViewPages.Adapter.RecyclerView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
/**
 * https://qiita.com/mss634/items/7f1750e332b3e06f8ff0
 */

class InfiniteScrollListener(
        val layoutManager: androidx.recyclerview.widget.LinearLayoutManager,
        val func: () -> Unit /*呼び出し元でcallback引数として{}を実装*/) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 2
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy > 0) {
            visibleItemCount = recyclerView.childCount;
            totalItemCount = layoutManager.itemCount;
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold /*最後の行から２つ上*/)) {
                // End has been reached
                Log.i("InfiniteScrollListener", "End reached")
                func() //呼び出し元で実装
                loading = true
            }
        }
    }

}