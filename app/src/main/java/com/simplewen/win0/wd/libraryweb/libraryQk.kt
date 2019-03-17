package com.simplewen.win0.wd.libraryweb

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AlertDialog

import android.view.MenuItem
import android.view.View
import android.widget.*

import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.modal.PreData

import kotlinx.android.synthetic.main.activity_library_qk.*

import request.requestManage

/**
 * 期刊搜索
 */
class libraryQk : AppCompatActivity() {
    val b_info = arrayOf("search_b_name", "search_b_company", "search_b_author", "search_b_number")
    val b_id = intArrayOf(R.id.qk_b_name, R.id.qk_b_company, R.id.qk_b_author, R.id.qk_b_number)
    private fun Tos(str: String) {
        Toast.makeText(this@libraryQk, str, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_qk)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val requset = requestManage(this@libraryQk)
        val qkSearch = findViewById<SearchView>(R.id.qk_search)
        var temLoadView: AlertDialog? = null
        val searchTips = findViewById<TextView>(R.id.searchTips)

        var loadView = AlertDialog.Builder(this)
        loadView.setView(R.layout.load)
                .create()

        val search_list = findViewById<ListView>(R.id.qk_list)//期刊列表
        var listadapter = SimpleAdapter(this@libraryQk, requset.qkBooks, R.layout.qk_list, b_info, b_id)
        search_list.adapter = listadapter//listview适配器
        val hand_search: Handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg?.what) {

                    PreData.NET_CODE_DATA_OK -> {
                        if (requset.qkBooks.size < 1) {
                            Tos("没有找到哦！")
                        }
                        temLoadView!!.dismiss()
                        listadapter.notifyDataSetChanged()

                    }
                    PreData.NET_CODE_DATA_ERROR -> {
                        Tos("连接失败!")
                    }

                }
            }

        }
        qkSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isNotEmpty()) {
                    temLoadView = loadView.show()
                    searchTips.visibility = View.GONE
                    requset.getQk(hand_search, query)
                } else {
                    Tos("请输入内容！")
                }

                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
