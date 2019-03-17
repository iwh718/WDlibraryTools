package com.simplewen.win0.wd.activity

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.modal.PreData
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_search_login.*
import request.requestManage

class search_login : AppCompatActivity() {
    val b_info = arrayOf("search_b_name", "search_b_number","search_b_author","search_b_time")
    val b_id = intArrayOf(R.id.search_b_name, R.id.search_b_number, R.id.search_b_author, R.id.search_b_time)
    private fun Tos(str:String){
        Toast.makeText(this@search_login,str,Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_login)
        setSupportActionBar(toolbar)
       toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val requset= requestManage(this@search_login)
        var s_books=requset.s_books
        var bookInfo = requset.bookInfo//图书详情
        var search_list=findViewById<ListView>(R.id.search_list)//搜索
        var listadapter3 = SimpleAdapter(this@search_login, s_books, R.layout.search_list, b_info, b_id)
        search_list.adapter = listadapter3//listview适配器
        val hand_search: Handler = object : Handler(Looper.getMainLooper()) {

                override fun handleMessage(msg: Message?) {
                    super.handleMessage(msg)
                    when (msg?.what) {

                        PreData.NET_CODE_DATA_OK -> {
                            if(s_books.size < 1 ){
                                Toast.makeText(this@search_login,"没有找到，换一下搜索模式？",Toast.LENGTH_SHORT).show()
                            }
                            listadapter3.notifyDataSetChanged()

                        }
                        PreData.BOOKS_INFO -> {
                            when(msg.arg1){
                                0 ->{
                                    Tos("这本书没有提供简介哦！")
                                }
                                1->{
                                    bookInfo = requset.bookInfo//更新简介
                                    // Tos("获取到简介")
                                    AlertDialog.Builder(this@search_login).setTitle("本书简介")
                                            .setMessage(bookInfo).create().show()
                                    //有简介，弹出alert
                                }
                            }
                        }
                        PreData.NET_CODE_DATA_ERROR -> {
                            Tos("网络数据异常，请稍后再试！")

                        }
                    }
                }

        }
        val temIntent:Intent=intent
        val search_mode = temIntent.getStringExtra("search_mode")
        val search_sort = temIntent.getStringExtra("search_sort")
        val search_key=temIntent.getStringExtra("search_key")

        search_list.setOnItemClickListener{
            _,_,position,_ ->
            requset.bookInfo("${s_books[position]["search_b_id"]}",hand_search)

        }
       Utils.Tos("$search_key：搜索中···")
        requset.mySearch(hand_search,search_key,search_mode,search_sort)

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

