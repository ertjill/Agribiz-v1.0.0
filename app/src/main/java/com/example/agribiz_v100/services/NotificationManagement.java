package com.example.agribiz_v100.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.agribiz_v100.ChatActivity;
import com.example.agribiz_v100.NotificationBroadcastReceiver;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.entities.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class NotificationManagement {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "newProductAdded" ;

    public static void newProductNotification (Context context, String title, String message, Intent intent) {
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int NOTIFICATION_ID = ( int ) System. currentTimeMillis () ;
        PendingIntent pendingIntent = PendingIntent. getActivity ( context, 0 , new Intent() , 0 ) ;
        Intent buttonIntent = new Intent( context, NotificationBroadcastReceiver.class ) ;
        buttonIntent.putExtra( "notificationId" , NOTIFICATION_ID) ;
        PendingIntent btPendingIntent = PendingIntent. getBroadcast ( context, 0 , buttonIntent , 0 ) ;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( context.NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context , default_notification_channel_id ) ;
        mBuilder.setContentTitle( title ) ;
        mBuilder.setContentIntent(pendingIntent) ;
        mBuilder.addAction(R.mipmap.ic_launcher_foreground , "Cancel" , btPendingIntent) ;
        mBuilder.setContentText( message) ;
        mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        mBuilder.setAutoCancel( true ) ;
        mBuilder.setDeleteIntent(getDeleteIntent(context)) ;
        mBuilder.setContentIntent(resultPendingIntent);
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID , mBuilder.build()) ;
    }

    protected static PendingIntent getDeleteIntent (Context context) {
        Intent intent = new Intent(context,
                NotificationBroadcastReceiver.class ) ;
        intent.setAction( "notification_cancelled" ) ;
        return PendingIntent. getBroadcast (context, 0 , intent , PendingIntent. FLAG_CANCEL_CURRENT ) ;
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "studdentChannel";
            String description = "Channel for studdent notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Ert", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void getNewMessageNotification(Context context, ChatModel chatModel, int id) {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, ChatActivity.class);
        resultIntent.putExtra("userId",chatModel.getChatSenderUserId());
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        ProfileManagement.getUserProfile(chatModel.getChatSenderUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String paths = documentSnapshot.getString("userImage");
                    Bitmap bitmap = null;
                    try {
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , Uri.parse(paths));
                    } catch (IOException e) {
                        e.printStackTrace();
//                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    
                    Toast.makeText(context, "Something", Toast.LENGTH_SHORT).show();
                    String name = documentSnapshot.getString("userDisplayName");
                    NotificationCompat.Builder notif = new NotificationCompat.Builder(context, "Ert")
                            .setSmallIcon(R.drawable.agribiz_logo_green)
                            .setContentTitle(name.substring(0, name.length() - 2))
                            .setContentText(chatModel.getChatMessage())
                            .setLargeIcon(bitmap)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(chatModel.getChatMessage()));

                    notif.setContentIntent(resultPendingIntent);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(id, notif.build());
                }
            }
        });

    }
}
