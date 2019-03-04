package com.meronmks.chairs.OAuth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.meronmks.chairs.R
import kotlinx.android.synthetic.main.activity_set_instance_name.*

class SetInstanceNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_instance_name)
    }

    fun addInstance(view: View){
        val intent = Intent(baseContext, LoginActivity::class.java)
        intent.putExtra("instanceName", instanceNameEditText.text.toString())
        startActivity(intent)
        finish()
    }
}
