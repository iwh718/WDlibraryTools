package com.simplewen.win0.wd.Fragment


import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplewen.win0.wd.R
import kotlinx.android.synthetic.main.activity_libraryweb.*
import kotlinx.android.synthetic.main.activity_libraryweb.view.*

/**
 * 首页动态
 */
class LibraryIndex: Fragment(){


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val webView = inflater!!.inflate(R.layout.activity_libraryweb,null).apply{
            libraryWebView.loadUrl("http://wendaedu.com.cn/tsg/m/info_102.html")
        }



        val webSet = webView.libraryWebView!!.settings
        webSet.javaScriptEnabled = true
        webView.visibility = View.INVISIBLE
        webView.libraryWebView!!.webViewClient = object: WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) = Unit
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //注入js，移除footer内容
                webView.libraryWebView!!.loadUrl("javascript: document.querySelector('.m-copy').setAttribute('style','display:none');" +
                        "document.querySelector('.u-tt .hd .col').setAttribute('style','display:none');document.querySelector('#header').setAttribute('style','display:none');" +
                        "document.querySelector('.swiper-container').setAttribute('style','display:none');")

                webView.webViewProgressbar.visibility = View.GONE
                webView.visibility = View.VISIBLE


            }

        }
        return webView
    }

}