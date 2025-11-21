package com.app.relatosfutbolerosnd.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RtmpBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        when (intent?.action) {
            ACTION_EXIT -> {
                // 2. EN LUGAR DE LLAMAR DIRECTAMENTE, ENVIAMOS UN COMANDO
                val stopIntent = Intent(context, RtmpStreamingService::class.java).apply {
                    action = ACTION_EXIT // Reusamos la misma acción
                }
                // Enviamos la orden al servicio
                context.startService(stopIntent)

                // 3. Abrimos la Activity de cierre (tu lógica original)
                val closeIntent = Intent(context, CloseActivity::class.java).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(closeIntent)
            }
        }
    }

    companion object {
        const val ACTION_EXIT = "ACTION_EXIT"
    }
}