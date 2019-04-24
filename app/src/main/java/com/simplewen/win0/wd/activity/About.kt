package com.simplewen.win0.wd.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

import android.net.Uri


/**
 * 关于页面
 */
@ExperimentalCoroutinesApi
class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        joinIwh.setOnClickListener {
            Utils.joinQQGroup(1)
        }
        github.setOnClickListener {
            val uri = Uri.parse("https://github.com/iwh718/WDlibraryTools")
            Intent(Intent.ACTION_VIEW, uri).apply {
                startActivity(this)
            }
        }
    }
}
