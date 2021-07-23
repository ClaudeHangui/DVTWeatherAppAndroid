package com.changui.dvtweatherappandroid.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import java9.util.function.BiFunction

class InternetAccessLiveData<T>(
    private val context: Context,
    private val filter: IntentFilter,
    private val mapFunc: BiFunction<Context, Intent, T>
) : MutableLiveData<T>() {

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(mBroadcastReceiver)
    }

    override fun onActive() {
        super.onActive()
        value = mapFunc.apply(context, Intent()) // some (like Functional Interfaces and java.util.function) are still restricted to APIs 24+.
        context.registerReceiver(mBroadcastReceiver, filter)
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // value = context?.let { intent?.let { it1 -> mapFunc.apply(it, it1) } }
            value = mapFunc.apply(context, intent)
        }
    }
}