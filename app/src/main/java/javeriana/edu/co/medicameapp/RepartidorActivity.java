package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javeriana.edu.co.medicameapp.adapters.OrderListAdapter;
import javeriana.edu.co.medicameapp.databinding.ActivityRepartidorBinding;

public class RepartidorActivity extends AppCompatActivity {

    DatabaseReference usersRef;
    OrderListAdapter adapter;
    Set<String> usuarios = new LinkedHashSet<>(); // Cambiar a un conjunto para evitar duplicados
    Set<String> usuarios2 = new LinkedHashSet<>(); // Cambiar a un conjunto para evitar duplicados


    ValueEventListener usersListener = new ValueEventListener() {
        private static final String TAG = "RepartidorActivity";

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            adapter.clear();
            String uid2 = "";
            int i = 0;
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                if (userSnapshot.child("estado").getValue(String.class).equals("Pendiente")) {
                    uid2 = userSnapshot.child("usuarioSoliciante").getValue(String.class);

                    if (usuarios.contains(uid2) == false) { // Verificar si el UID no está en el conjunto antes de agregarlo
                        usuarios.add(uid2); // Agregar el UID del usuario al conjunto
                        i++;
                    }
                        Log.d("Pendientes Lista", "Pedido pendiente " + i + "---" + userSnapshot.getKey());
                        // Consultar los nombres de usuario de forma asíncrona
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");

                        for (String uid : usuarios) {

                            String finalUid = uid2;
                            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    if (dataSnapshot.exists()) {
                                        String nombre = dataSnapshot.child("name").getValue(String.class);
                                        Log.d(TAG, "Nombre: " + nombre);
                                        adapter.add(nombre + "|" + uid); // Agregar el nombre al adaptador
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("Error al acceder a la base de datos: " + databaseError.getMessage());
                                }
                            });
                        }
                } else {
                    Log.d(TAG, "No hay pedidos pendientes");
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "onCancelled", databaseError.toException());
        }
    };


    ActivityRepartidorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRepartidorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        usersRef = databaseRef.child("orders");

        adapter = new OrderListAdapter(this, R.layout.order_row, new ArrayList<String>());

        ArrayList<String> elementos = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            elementos.add(adapter.getItem(i));
        }

        ArrayList<String> elementosSinDuplicados = new ArrayList<>();

        for (String elemento : elementos) {
            if (!elementosSinDuplicados.contains(elemento)) {
                elementosSinDuplicados.add(elemento);
            }
        }

        ArrayAdapter<String> nuevoAdapter = new ArrayAdapter<>(this, R.layout.order_row, elementosSinDuplicados);

        adapter = new OrderListAdapter(this, R.layout.order_row, elementosSinDuplicados);



        usersRef.addValueEventListener(usersListener);

        ListView listView = binding.listaPedidos;
        listView.setAdapter(adapter);


        binding.logOut2.setOnClickListener((view) -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(RepartidorActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        usersRef.removeEventListener(usersListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersRef.addValueEventListener(usersListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        usersRef.removeEventListener(usersListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersRef.removeEventListener(usersListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        usersRef.addValueEventListener(usersListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        usersRef.addValueEventListener(usersListener);
    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}