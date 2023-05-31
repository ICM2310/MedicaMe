package javeriana.edu.co.medicameapp.notifications;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import javeriana.edu.co.medicameapp.MenuActivity;
import javeriana.edu.co.medicameapp.R;

public class NotificacionesBackground extends Service {

    private Handler handler;
    private Runnable runnable;
    private int userNotificationCount = 2;
    private DatabaseReference database;


    // Service stuff

    @Override
    public void onCreate() {
        super.onCreate();

        initRealtimeDB();
        createNotificationChannel();

        Log.i("Notifications", "ListenerService - BOOT Service has been started for MedicaMe");

        handler = new Handler();
        runnable = () -> {
            Log.d("Notifications", "ListenerService - Listener Service IS RUNNING -> Log message every 5 secs (It stops the process from dying)");
            handler.postDelayed(runnable, 5000);
        };
    }

    public void initRealtimeDB() {
        database = FirebaseDatabase.getInstance().getReference();
        Log.d("Notifications", "RealtimeDB init done.");
        // subscribeToChanges();
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}

    public void onDestroy() {

        Log.i("Notifications", "BOOT Service has been stopped");
        // Toast.makeText(this, "BOOT service stopped", Toast.LENGTH_LONG).show();

        // unSubscribeToChanges();

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Notifications", "ListenerService - BOOT Service has been started - onStartCommand");

        // Toast.makeText(this, "ListenerService Started", Toast.LENGTH_LONG).show();

        handler.post(runnable);

        Intent intent2 = new Intent(this, MenuActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(com.google.firebase.database.R.drawable.common_google_signin_btn_text_dark)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setContentTitle("MedicaMe!")
                .setContentText("MedicaMe está para ayudarte a pedir los medicamentos que necesitas!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true); // Make the notification sticky



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        startForeground(1, builder.build());


        return START_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ListenerNotiChannel";
            String description = "ListenerNotiChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
