package com.meronmks.chairs.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.deploygate.sdk.DeployGate
import com.meronmks.chairs.BuildConfig
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by meron on 2018/02/15.
 * トースト表示に関する拡張メソッド
 */

/**
 * 普通に表示させるだけ
 */
fun String.showToast(context: Context?, duration: Int){
    val msg = this
    launch(UI) {
        Toast.makeText(context, msg, duration).show()
    }
}

/**
 * デバッグ表示
 */
fun String.showToastLogD(context: Context?, tag: String = "Debug"){
    val msg = this
    if (BuildConfig.DEBUG){
        Log.d(tag, msg)
    }
    launch(UI) {
        DeployGate.logDebug(msg)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}

/**
 * エラー表示
 */
fun String.showToastLogE(context: Context?, tag: String = "Error"){
    val msg = this
    if (BuildConfig.DEBUG){
        Log.e(tag, msg)
    }
    launch(UI) {
        DeployGate.logError(msg)
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}