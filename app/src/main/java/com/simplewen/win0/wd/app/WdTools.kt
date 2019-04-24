package com.simplewen.win0.wd.app
import android.app.Application
import android.content.Context
import com.simplewen.win0.wd.request.RequestManage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.annotations.NotNull
import kotlin.properties.Delegates

/**
 * 单例
 */
@ExperimentalCoroutinesApi
class WdTools:Application(){

    companion object {

        var  _context:Application by Delegates.notNull()
       lateinit var MainRequest:RequestManage

        //返回Context
        fun getContext():Context{
            return _context
        }
        //返回全局网络管理对象
        fun getRequest():RequestManage{

            return MainRequest
        }
        //设置全局网络管理对象
        fun setRequest(currentMainRequest:RequestManage){
            MainRequest = currentMainRequest
        }
    }
    override fun onCreate() {
        super.onCreate()
        _context = this
    }



}