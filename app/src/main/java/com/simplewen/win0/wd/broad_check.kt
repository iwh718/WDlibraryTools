package com.simplewen.win0.wd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Network



class check_broad:BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE)

    }



}