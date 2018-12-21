package com.simplewen.win0.wd.libraryweb

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplewen.win0.wd.R

class libraryweb : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libraryweb)
        val webLibrary  = findViewById<WebView>(R.id.libraryWebView)
        val webSet = webLibrary.settings
        webSet.javaScriptEnabled = true
        webLibrary.loadUrl("http://wendaedu.com.cn/tsg/m/info_102.html")
        webLibrary.webViewClient = object:WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                //
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webLibrary.loadUrl("javascript: document.querySelector('.m-copy').setAttribute('style','display:none');")

            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //
                return false
            }
        }
    }
}
