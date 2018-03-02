package com.meronmks.chairs.initialize

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.meronmks.chairs.OAuth.SetInstanceNameActivity
import com.meronmks.chairs.Tools.Database.AccountDataBaseTool
import com.meronmks.chairs.ViewPages.HomeViewPage

class MainActivity : AppCompatActivity() {

    lateinit var accountDataBase: AccountDataBaseTool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountDataBase = AccountDataBaseTool(this)
        val accounts = accountDataBase.readAccounts()
        if (accounts.count() == 0){
            val intent = Intent(this, SetInstanceNameActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, HomeViewPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accountDataBase.close()
    }
}
