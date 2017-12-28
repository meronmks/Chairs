package com.meronmks.chairs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mRealm : Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)
        mRealm = Realm.getDefaultInstance()
        testTextView.text = mRealm.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
}
