package javeriana.edu.co.medicameapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import javeriana.edu.co.medicameapp.R;

public class notificationsHelper {

    private static final String CHANNEL_ID = "Your_Channel_ID";
    private static final String CHANNEL_NAME = "Your_Channel_Name";
    private static final String CHANNEL_DESCRIPTION = "Your_Channel_Description";
    private static boolean channelCreated = false;

    public static void showOrderNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel if not created already
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !channelCreated) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
            channelCreated = true;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.delivery)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.delivery))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        // Show the notification
        notificationManager.notify(2, builder.build());
    }
}

