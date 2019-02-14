package com.simplewen.win0.wd.modal

import android.app.Activity
import com.simplewen.win0.wd.app.CloudApp

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
         * @return 返回伴生对象
         * **/
        fun<T> setSHP(saveKey:String,saveText:T,shpName:String):iwhDataOperator.Companion{
            //打开指定文件
            val SHP_Text =  CloudApp.getContext().getSharedPreferences(shpName, Activity.MODE_PRIVATE)
                when(saveText){
                   is Int ->{
                        //存放整型数据
                       SHP_Text.edit().putInt(saveKey,saveText).apply()
                    }
                    //存放字符型数据
                    is String ->{
                        SHP_Text.edit().putString(saveKey,saveText).apply()
                    }
                    else ->{
                        throw Throwable("类型不匹配！")
                    }

                }
                //链式调用
                return iwhDataOperator.Companion
        }
        /**获取私有数据
         * @param getKey 获取指定文件指定键
         * @param type 取出指定类型值
         * @param shpName 获取指定文件**/
        fun<T> getSHP(getKey:String,shpName:String,type:T):T{
            val SHP_Text =  CloudApp.getContext().getSharedPreferences(shpName, Activity.MODE_PRIVATE)
            when(type){
                is Int ->{
                    //返回整型数据
                return SHP_Text.getInt(getKey,0) as T
                }
                //返回字符型数据
                is String ->{
                   return SHP_Text.getString(getKey,"") as T
                }
                //返回布尔值数据
                is Boolean ->{
                    return  SHP_Text.getBoolean(getKey,false) as T
                }
                //抛出异常
                else ->{
                    throw Throwable("类型不匹配！")
                }

            }
        }

    }


}