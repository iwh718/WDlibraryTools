package com.simplewen.win0.wd.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView

import com.simplewen.win0.wd.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        search.isSubmitButtonEnabled = true
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?) = true

            override fun onQueryTextSubmit(query: String?): Boolean {
              return true
            }
        })
    }
}
