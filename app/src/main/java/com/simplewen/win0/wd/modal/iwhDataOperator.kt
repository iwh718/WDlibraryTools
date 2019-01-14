package com.simplewen.win0.wd.modal

import android.app.Activity
import com.simplewen.win0.wd.WdTools

/**数据操作类
 @author:iwh
 @time:2019.01.10
 **/
class iwhDataOperator{

    companion object {
        /**操作SharePreferences
         * @param saveKey 存储键
         * @param saveText 存储数据
         * @param shpName 指定文件
         * **/
        fun<T> setSHP(saveKey:String,saveText:T,shpName:String):iwhDataOperator.Companion{
            //打开指定文件
            val SHP_Text =  WdTools.getContext().getSharedPreferences(shpName, Activity.MODE_PRIVATE)
                when{
                    saveText is Int ->{
                        //存放整型数据
                       SHP_Text.edit().putInt(saveKey,saveText).apply()
                    }
                    //存放字符型数据
                    saveText is String ->{
                        SHP_Text.edit().putString(saveKey,saveText).apply()
                    }
                    else ->{
                        throw Throwable("类型不匹配！")
                    }

                }
                //链式调用
                return iwhDataOperator.Companion
        }

    }


}