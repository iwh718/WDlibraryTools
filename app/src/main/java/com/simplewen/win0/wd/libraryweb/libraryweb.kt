package com.simplewen.win0.wd.libraryweb

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.WDMain
import kotlinx.android.synthetic.main.activity_libraryweb.*

class libraryweb : AppCompatActivity() {
    private var webLibrary:WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libraryweb)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        webLibrary  = findViewById<WebView>(R.id.libraryWebView)
        val refresh = findViewById<SwipeRefreshLayout>(R.id.libraryWebViewRefresh)//刷新
        refresh.setOnRefreshListener {
            webLibrary!!.reload()
        }
        refresh.setColorSchemeResources(R.color.colorAccent)
        val webSet = webLibrary!!.settings
        webSet.javaScriptEnabled = true
        webLibrary!!.loadUrl("http://wendaedu.com.cn/tsg/m/info_102.html")
        webLibrary!!.webViewClient = object:WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                //
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webLibrary!!.loadUrl("javascript: document.querySelector('.m-copy').setAttribute('style','display:none');" +
                        "document.querySelector('.u-tt .hd .col').setAttribute('style','display:none');")
                refresh.isRefreshing = false

            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //
                return false
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && webLibrary!!.canGoBack()){
            webLibrary!!.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> {
               startActivity(Intent(this@libraryweb,WDMain::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
