package com.simplewen.win0.wd.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.modal.PreData
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_history.*
import request.requestManage

/**
 * 我的借阅历史
 */
class history : AppCompatActivity() {
    val b_info = arrayOf("h_name", "h_number")
    val b_id = intArrayOf(R.id.h_name, R.id.h_number)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        var requset=requestManage(this@history)
        var h_books=requset.h_books
        var history_list=findViewById<ListView>(R.id.history_list)//借阅历史列表
        var listadapter2 = SimpleAdapter(this, h_books, R.layout.history_list, b_info, b_id)
        history_list.adapter = listadapter2 //listview适配器
        var hand_history: Handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg?.what) {
                    PreData.NET_CODE_DATA_OK -> {
                        listadapter2.notifyDataSetChanged()

                    }
                    PreData.NET_CODE_DATA_ERROR->{
                        Utils.Tos("网络连接失败！")
                    }
                }
            }

        }

        requset.myHisory(hand_history)

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
