package com.simplewen.win0.wd.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simplewen.win0.wd.R
import kotlinx.android.synthetic.main.china_zhiwang.view.*

/**
 * 知网webView嵌入。。。
 */
class ChinaZhiWang: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ly = inflater!!.inflate(R.layout.china_zhiwang,null)
        ly.ChinaZhiWangWebView.loadUrl("http://wap.cnki.net/touch/web")
        return ly
    }
}