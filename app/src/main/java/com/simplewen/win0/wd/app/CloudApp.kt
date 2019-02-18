package com.simplewen.win0.wd.app
import android.app.Application
import android.content.Context
import request.requestCLoudLibrary


class CloudApp:Application(){
    companion object {
        var  _context:Application? = null
        var requestAll:requestCLoudLibrary? = null
        fun getContext():Context{
            return _context!!
        }
    }
    override fun onCreate() {
        super.onCreate()
        _context = this

    }



}