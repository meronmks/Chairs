package com.meronmks.chairs.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.deploygate.sdk.DeployGate
import com.meronmks.chairs.BuildConfig
import kotlinx.coroutines.*

/**
 * Created by meron on 2018/02/15.
 * トースト表示に関する拡張メソッド
 */

/**
 * 普通に表示させるだけ
 */
fun String.showToast(context: Context?, duration: Int, tag: String = "Info"){
    val msg = this
    if (BuildConfig.DEBUG){
        Log.i(tag, msg)
    }
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(context, msg, duration).show()
    }
}

/**
 * Info表示（DeployGateに投げ込む用）
 */
fun String.showToastLogI(context: Context?, duration: Int, tag: String = "Info"){
    val msg = this
    if (BuildConfig.DEBUG){
        DeployGate.logInfo(msg)
        Log.i(tag, msg)
    }
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(context, msg, duration).show()
    }
}

/**
 * デバッグ表示
 */
fun String.showToastLogD(context: Context?, tag: String = "Debug"){
    val msg = this
    if (BuildConfig.DEBUG){
        DeployGate.logDebug(msg)
        Log.d(tag, msg)
    }
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}

/**
 * エラー表示
 */
fun String.showToastLogE(context: Context?, tag: String = "Error"){
    val msg = this
    if (BuildConfig.DEBUG){
        DeployGate.logError(msg)
        Log.e(tag, msg)
    }
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}