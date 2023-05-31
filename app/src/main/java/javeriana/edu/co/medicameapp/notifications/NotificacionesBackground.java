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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firestore.admin.v1.Index;

import java.util.ArrayList;

import javeriana.edu.co.medicameapp.MenuActivity;
import javeriana.edu.co.medicameapp.R;
import javeriana.edu.co.medicameapp.modelos.Order;

public class NotificacionesBackground extends Service {

    private Handler handler;
    private Runnable runnable;
    private int userNotificationCount = 2;
    private DatabaseReference database;
    private FirebaseAuth mAuth;


    ChildEventListener orderChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {


            // Se invoca cuando se agrega un nuevo pedido
            String orderId = dataSnapshot.getKey();
            String usuarioSoliciante = dataSnapshot.child("usuarioSoliciante").getValue(String.class);
            String usuarioRepartidor = dataSnapshot.child("usuarioRepartidor").getValue(String.class);
            String estado = dataSnapshot.child("estado").getValue(String.class);

            Log.i("Notifications", "Order Added: " + dataSnapshot.getValue());
            Log.i("Notifications", "Current user id: " + mAuth.getUid());

            if (usuarioSoliciante.equals(mAuth.getUid())){
                Log.i("Notifications", "La orden es para el usuario actual");
            };

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {

            // Se invoca cuando se agrega un nuevo pedido
            String orderId = dataSnapshot.getKey();
            String usuarioSoliciante = dataSnapshot.child("usuarioSoliciante").getValue(String.class);
            String usuarioRepartidor = dataSnapshot.child("usuarioRepartidor").getValue(String.class);
            String estado = dataSnapshot.child("estado").getValue(String.class);

            Log.i("Notifications", "Order Changed: " + dataSnapshot.getValue());
            Log.i("Notifications", "Current user id: " + mAuth.getUid());

            if (usuarioSoliciante.equals(mAuth.getUid())){
                Log.i("Notifications", "La orden es para el usuario actual");

                if (estado.equals("Asignado")){
                    Log.i("Notifications", "Repartidor asignado a la orden del usuario actual");

                    // Crear notificacion.
                    notificationsHelper.showOrderNotification(getBaseContext(), "Orden asignada", "Su orden ha sido asignada a un repartidor");

                }
            };
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            Log.i("Notifications", "Order removed");

            // Se invoca cuando un pedido existente es eliminado
            String orderId = dataSnapshot.getKey();

            // Lógica para el pedido eliminado
            System.out.println("Pedido eliminado:");
            System.out.println("ID del pedido: " + orderId);
            System.out.println("--------------------------");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Maneja los errores si ocurren durante la escucha de cambios en los pedidos
            System.out.println("Error en la escucha de cambios en los pedidos: " + databaseError.getMessage());
        }
    };



    // Service stuff

    @Override
    public void onCreate() {
        super.onCreate();

        initRealtimeDB();
        createNotificationChannel();

        Log.i("Notifications", "ListenerService - BOOT Service has been started for MedicaMe");

        handler = new Handler();
        runnable = () -> {
            //Log.d("Notifications", "ListenerService - Listener Service IS RUNNING -> Log message every 5 secs (It stops the process from dying)");
            handler.postDelayed(runnable, 5000);
        };

        mAuth = FirebaseAuth.getInstance();
    }


    public void initRealtimeDB() {
        database = FirebaseDatabase.getInstance().getReference();
        Log.d("Notifications", "RealtimeDB init done.");
        subscribeToChanges();
    }

    public void subscribeToChanges() {
        // Add already defined listener
        FirebaseDatabase.getInstance().getReference().child("orders").addChildEventListener(orderChildEventListener);
        Log.d("RealtimeDB", "Subbed to changes.");
    }

    public void unSubscribeToChanges() {
        // Add already defined listener
        FirebaseDatabase.getInstance().getReference().child("orders").removeEventListener(orderChildEventListener);
        Log.d("RealtimeDB", "UNSubbed to changes.");
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
                .setSmallIcon(R.drawable.delivery)
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
