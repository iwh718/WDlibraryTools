package com.simplewen.win0.wd

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_search_login.*
import request.requestManage

class search_login : AppCompatActivity() {
    val b_info = arrayOf("search_b_name", "search_b_number","search_b_author","search_b_time")
    val b_id = intArrayOf(R.id.search_b_name,R.id.search_b_number,R.id.search_b_author,R.id.search_b_time)
   // var bname="php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_login)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        var requset= requestManage(this@search_login)
        var s_books=requset.s_books
        var search_list=findViewById<ListView>(R.id.search_list)//搜索
        var listadapter3 = SimpleAdapter(this@search_login, s_books, R.layout.search_list, b_info, b_id)
        search_list.adapter = listadapter3//listview适配器
        var hand_search: Handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg?.what) {

                    6 -> {
                        listadapter3.notifyDataSetChanged()

                    }
                }
            }

        }
        val temIntent:Intent=getIntent()
        val search_key=temIntent.getStringExtra("search_key")
        Snackbar.make(search_list,search_key+"：搜索中···",Snackbar.LENGTH_LONG).show()

        requset.mySearch(hand_search,search_key)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    }

