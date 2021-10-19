package com.gdsc_technion.gdsc_app

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.e("message", "Message Received")
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("token", "New Token")
    }
}