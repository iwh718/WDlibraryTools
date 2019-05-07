package com.simplewen.win0.wd.app
import android.app.Application
import android.content.Context

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.properties.Delegates

/**
 * 单例
 */
@ExperimentalCoroutinesApi
class WdTools:Application(){

    companion object {

        var  _context:Application by Delegates.notNull()


        //返回Context
        fun getContext():Context{
            return _context
        }

    }
    override fun onCreate() {
        super.onCreate()
     //   LeakCanary.install(this)
        _context = this

    }



}