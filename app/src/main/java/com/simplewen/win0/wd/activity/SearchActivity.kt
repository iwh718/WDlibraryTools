package com.simplewen.win0.wd.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.SearchView

import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.adapter.SearchListViewAdapter
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_search.*
import request.requestCLoudLibrary

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val request = requestCLoudLibrary()
        val searchListAdapter = SearchListViewAdapter(request.searchBooks)
        searchListView.adapter = searchListAdapter
        val handler = Handler{
            when(it.what){
                //搜索完成
                0x1 ->{
                    searchListAdapter.notifyDataSetChanged()

                    true
                }
                //异常
                0x2 ->{
                    true
                }
                //网络连接失败
                0x3 ->{
                    Utils.Tos("没有找到哦！")
                    true
                }
                else -> true
            }
        }
        search.isSubmitButtonEnabled = true
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?) = true

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if(it.isNotEmpty()){
                        request.search(handler,it)
                    }else{
                        Utils.Tos("请输入数据！")
                    }
                }
              return true
            }
        })

        with(searchListView){

        }
    }
}
