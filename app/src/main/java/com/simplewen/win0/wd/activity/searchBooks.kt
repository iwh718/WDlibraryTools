package com.simplewen.win0.wd.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleAdapter
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.request.WorkWd
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_search_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 搜索页面
 */
@ExperimentalCoroutinesApi
class searchBooks : BaseActivity() {

    val b_info = arrayOf("search_b_name", "search_b_number", "search_b_author", "search_b_time")
    val b_id = intArrayOf(R.id.search_b_name, R.id.search_b_number, R.id.search_b_author, R.id.search_b_time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_login)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val s_books =WorkWd.allSearchBooks

        val search_list = findViewById<ListView>(R.id.search_list)//搜索
        val listadapter3 = SimpleAdapter(this@searchBooks, s_books, R.layout.search_list, b_info, b_id)
        search_list.adapter = listadapter3//listview适配器

        val temIntent: Intent = intent
        val search_key = temIntent.getStringExtra("search_key")

        search_list.setOnItemClickListener { _, _, position, _ ->
            WorkWd.bookInfo("${s_books[position]["search_b_id"]}", this@searchBooks)

        }
        Utils.Tos("$search_key：搜索中···")
       launch {
           val searchJob =   WorkWd.mySearch(search_key,coroutines =   this@searchBooks, adapter = listadapter3)
           searchJob.join()


           Log.d("@@searchJobOver","===========")
       }


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

