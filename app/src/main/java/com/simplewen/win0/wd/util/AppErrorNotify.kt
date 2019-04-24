package com.simplewen.win0.wd.util

import com.simplewen.win0.wd.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 异常
 */
@ExperimentalCoroutinesApi
interface AppErrorNotify{
   fun showError(coroutines:BaseActivity,errorTex:String = "网络异常！"):Job
}
@ExperimentalCoroutinesApi
class NetError: AppErrorNotify {

    override fun showError(coroutines: BaseActivity,errorTex: String): Job = coroutines.launch(Dispatchers.Main) {
        Utils.Tos(errorTex)
    }
}