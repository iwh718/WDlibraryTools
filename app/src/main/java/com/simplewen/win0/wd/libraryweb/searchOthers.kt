package com.simplewen.win0.wd.libraryweb

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplewen.win0.wd.R

class searchOthers : AppCompatActivity() {
    lateinit var webBox:WebView
    lateinit var  webUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       webUrl = intent.getStringExtra("webUrl")
        setContentView(R.layout.activity_search_others)
        webBox  = findViewById<WebView>(R.id.searchOthersWebView)
        webBox.settings.javaScriptEnabled = true
        webBox.loadUrl(webUrl)
        webBox.webViewClient = object :WebViewClient(){

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?){
                webBox.loadUrl("javascript: document.querySelector('.m-copy').setAttribute('style','display:none');" +
                        "document.querySelector('.u-tt .hd .col').setAttribute('style','display:none');")
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && webBox.canGoBack()){
           webBox.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.web_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.openWeb ->{
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val content_url = Uri.parse(webUrl)
                intent.data = content_url
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
