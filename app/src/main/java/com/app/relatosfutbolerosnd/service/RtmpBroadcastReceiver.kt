package com.app.relatosfutbolerosnd.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RtmpBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("RtmpReceiver", "onReceive disparado con acción: ${intent?.action}")

        if (context == null) return

        // CORRECCIÓN PRINCIPAL:
        // Usamos la misma constante que define el Servicio: RtmpStreamingService.ACTION_STOP_STREAM
        // Antes estabas esperando "ACTION_EXIT", pero la notificación enviaba "ACTION_STOP_STREAM"
        if (intent?.action == RtmpStreamingService.ACTION_STOP_STREAM) {

            Log.d("RtmpReceiver", "Acción correcta recibida. Deteniendo todo...")

            // 1. Enviar orden de STOP al Servicio
            // Usamos la misma acción ACTION_STOP_STREAM que el servicio sabe manejar en onStartCommand
            val stopServiceIntent = Intent(context, RtmpStreamingService::class.java).apply {
                action = RtmpStreamingService.ACTION_STOP_STREAM
            }
            context.startService(stopServiceIntent)

            // 2. Abrir CloseActivity para matar la app
            val closeIntent = Intent(context, CloseActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(closeIntent)
        }

//        when (intent?.action) {
//            ACTION_EXIT -> {
//                // 2. EN LUGAR DE LLAMAR DIRECTAMENTE, ENVIAMOS UN COMANDO
//                val stopIntent = Intent(context, RtmpStreamingService::class.java).apply {
//                    action = ACTION_EXIT // Reusamos la misma acción
//                }
//                // Enviamos la orden al servicio
//                context.startService(stopIntent)
//
//                // 3. Abrimos la Activity de cierre (tu lógica original)
//                val closeIntent = Intent(context, CloseActivity::class.java).apply {
//                    addFlags(FLAG_ACTIVITY_NEW_TASK)
//                }
//                context.startActivity(closeIntent)
//            }
//        }
    }

    companion object {
        const val ACTION_EXIT = "ACTION_EXIT"
    }
}