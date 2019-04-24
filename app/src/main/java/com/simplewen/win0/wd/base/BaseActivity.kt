package com.simplewen.win0.wd.base
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * 基类
 */
@ExperimentalCoroutinesApi
abstract class BaseActivity:AppCompatActivity(), CoroutineScope by MainScope(){
    override fun onDestroy() {
        super.onDestroy()
        //退出时候取消主协程
        this@BaseActivity.cancel()
    }
}