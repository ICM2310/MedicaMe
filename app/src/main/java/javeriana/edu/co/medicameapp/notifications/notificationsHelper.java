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
    private static final String CHANNEL_NAME = "NotiChannel";
    private static final String CHANNEL_DESCRIPTION = "Notificaciones de pedidos";
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

    public static void showReservaNotification(Context context, String title, String message) {

        String CHANNEL_ID_reserva = "Reserva";
        String CHANNEL_NAME_reserva = "NotiChannelReserva";
        String CHANNEL_DESCRIPTION_reserva = "Notificaciones de Reserva";


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel if not created already
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !channelCreated) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_reserva, CHANNEL_NAME_reserva, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION_reserva);
            notificationManager.createNotificationChannel(channel);
            channelCreated = true;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_reserva)
                .setSmallIcon(R.drawable.farmacia)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.farmacia))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        // Show the notification
        notificationManager.notify(2, builder.build());
    }

    public static void showDomicilioNotification(Context context, String title, String message) {

        String CHANNEL_ID_reserva = "Domicilio";
        String CHANNEL_NAME_reserva = "NotiChannelDomicilio";
        String CHANNEL_DESCRIPTION_reserva = "Notificaciones de Domicilio";


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel if not created already
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !channelCreated) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_reserva, CHANNEL_NAME_reserva, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION_reserva);
            notificationManager.createNotificationChannel(channel);
            channelCreated = true;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_reserva)
                .setSmallIcon(R.drawable.deliverymarker)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.deliverymarker))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        // Show the notification
        notificationManager.notify(2, builder.build());
    }


}

