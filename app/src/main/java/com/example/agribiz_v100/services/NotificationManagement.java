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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.agribiz_v100.ChatActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class NotificationManagement {
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
