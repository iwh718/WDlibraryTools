package com.simplewen.win0.wd

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.app_bar_wd_main.*
import request.requestManage


class brow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        var tem_b_id:String=""
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brow)
        var toolbar = findViewById<Toolbar>(R.id.toolbar_brown)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        var request = requestManage(this@brow)//实例化请求对象
        val b_info = arrayOf("b_name", "b_last", "b_next", "b_continue","b_id")
        val b_id = intArrayOf(R.id.b_name, R.id.b_last, R.id.b_next, R.id.b_continue,R.id.b_id)
        var books = request.books//listview数组
        val tv_booklist = findViewById<ListView>(R.id.brow_list)//引用listview的书列表
        var listadapter = SimpleAdapter(this,books, R.layout.booklist, b_info, b_id)
        tv_booklist.adapter = listadapter //listview适配器



        var hand: Handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    3 -> {
                        Toast.makeText(this@brow,"请求失败", Toast.LENGTH_SHORT).show()
                    }

                    6 -> {
                        Toast.makeText(this@brow,"请求成功",Toast.LENGTH_SHORT).show()
                        listadapter.notifyDataSetChanged()

                    }
                    7->{
                        Toast.makeText(this@brow,"续借成功，重新进入界面刷新日期",Toast.LENGTH_SHORT).show()
                    }
                    8->Toast.makeText(this@brow,"续借失败，你已经续借过！",Toast.LENGTH_SHORT).show()
                }
            }

        }
        tv_booklist.onItemClickListener = object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var book_id:String=books[position].get("bid").toString()
                tem_b_id=book_id//传出编号
                var dialog = AlertDialog.Builder(this@brow)
                dialog.setTitle("你最多只能续借一次（30天）哦！")
                        .setNegativeButton("取消"){
                            _,_ ->

                        }
                        .setPositiveButton("确认") { _, _ ->
                            request.myContinue(tem_b_id,hand)
                        }
                        .create()
                dialog.show()
            }
        }

        request.myBrow(hand,listadapter)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
