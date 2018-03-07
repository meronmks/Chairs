package com.meronmks.chairs.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.deploygate.sdk.DeployGate
import com.meronmks.chairs.BuildConfig

/**
 * Created by meron on 2018/02/15.
 * トースト表示に関する拡張メソッド
 */

/**
 * 普通に表示させるだけ
 */
fun String.showToast(context: Context?, duration: Int){
    Toast.makeText(context, this, duration).show()
}

/**
 * デバッグ表示
 */
fun String.showToastLogD(context: Context?, tag: String = "Debug"){
    if (BuildConfig.DEBUG){
        Log.d(tag, this)
    }
    DeployGate.logDebug(this)
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

/**
 * エラー表示
 */
fun String.showToastLogE(context: Context?, tag: String = "Error"){
    if (BuildConfig.DEBUG){
        Log.e(tag, this)
    }
    DeployGate.logError(this)
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}