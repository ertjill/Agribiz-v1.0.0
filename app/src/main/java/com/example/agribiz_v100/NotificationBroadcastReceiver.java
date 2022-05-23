package com.example.agribiz_v100;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context , Intent intent) {
        int notificationId = intent.getIntExtra( "notificationId" , 0 ) ;
        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        manager.cancel(notificationId) ;
    }
}
