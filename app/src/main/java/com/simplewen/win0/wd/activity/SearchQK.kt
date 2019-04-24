package com.simplewen.win0.wd.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog

import android.view.MenuItem
import android.view.View
import android.widget.*

import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity

import kotlinx.android.synthetic.main.activity_library_qk.*

import com.simplewen.win0.wd.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlin.coroutines.CoroutineContext

/**
 * 期刊搜索
 */
@ExperimentalCoroutinesApi
class SearchQK : BaseActivity(),CoroutineScope by MainScope() {
    override val coroutineContext: CoroutineContext
        get() = super.coroutineContext
    lateinit var  temLoadView: AlertDialog
    val b_info = arrayOf("search_b_name", "search_b_company", "search_b_author", "search_b_number")
    val b_id = intArrayOf(R.id.qk_b_name, R.id.qk_b_company, R.id.qk_b_author, R.id.qk_b_number)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_qk)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val requset = WdTools.MainRequest
        val qkSearch = findViewById<SearchView>(R.id.qk_search)
        val searchTips = findViewById<TextView>(R.id.searchTips)

        val loadView = AlertDialog.Builder(this)
        loadView.setView(R.layout.load)
                .create()

        val search_list = findViewById<ListView>(R.id.qk_list)//期刊列表
        val listadapter = SimpleAdapter(this@SearchQK, requset.qkBooks, R.layout.qk_list, b_info, b_id)
        search_list.adapter = listadapter//listview适配器

        qkSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isNotEmpty()) {
                    temLoadView = loadView.show()
                    searchTips.visibility = View.GONE
                    requset.getQk(this@SearchQK, query,listadapter)
                } else {
                    Utils.Tos("请输入信息！")
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
