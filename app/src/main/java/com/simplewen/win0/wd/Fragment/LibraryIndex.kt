package com.simplewen.win0.wd.Fragment


import android.app.ActionBar
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.simplewen.win0.wd.R
import kotlinx.android.synthetic.main.activity_libraryweb.*
import kotlinx.android.synthetic.main.activity_libraryweb.view.*
import kotlin.concurrent.thread

/**
 * 首页动态
 */
class LibraryIndex : Fragment() {
lateinit var webView:WebView
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val ly = inflater!!.inflate(R.layout.activity_libraryweb, null).webViewLy
        webView =   WebView(context)
        val webSet = webView.settings
        webSet.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) = Unit
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //注入js，移除footer内容
                webView.loadUrl("javascript: document.querySelector('.m-copy').setAttribute('style','display:none');" +
                        "document.querySelector('.u-tt .hd .col').setAttribute('style','display:none');document.querySelector('#header').setAttribute('style','display:none');" +
                        "document.querySelector('.swiper-container').setAttribute('style','display:none');")
            }

        }
        webView.loadUrl("http://wendaedu.com.cn/tsg/m/info_102.html")

        ly.addView(webView)

        return ly
    }


}