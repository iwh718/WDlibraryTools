package com.simplewen.win0.wd.app
import android.app.Application
import android.content.Context


class CloudApp:Application(){
    companion object {
        var  _context:Application? = null
        fun getContext():Context{
            return _context!!
        }
    }
    override fun onCreate() {
        super.onCreate()
        _context = this
    }



}