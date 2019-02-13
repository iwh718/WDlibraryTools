package com.simplewen.win0.wd.libraryweb

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.AXGmain
import kotlinx.android.synthetic.main.activity_libraryweb.*

class libraryweb : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libraryweb)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        libraryWebViewRefresh.setOnRefreshListener {
            libraryWebView!!.reload()
        }
        libraryWebViewRefresh.setColorSchemeResources(R.color.colorAccent)
        val webSet = libraryWebView!!.settings
        webSet.javaScriptEnabled = true
        libraryWebView!!.loadUrl("http://wendaedu.com.cn/tsg/m/info_102.html")
        libraryWebView!!.webViewClient = object:WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) = Unit
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                libraryWebView!!.loadUrl("javascript: document.querySelector('.m-copy').setAttribute('style','display:none');" +
                        "document.querySelector('.u-tt .hd .col').setAttribute('style','display:none');")
                libraryWebViewRefresh.isRefreshing = false

            }

        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && libraryWebView!!.canGoBack()){
            libraryWebView!!.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> {
               startActivity(Intent(this@libraryweb,AXGmain::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
