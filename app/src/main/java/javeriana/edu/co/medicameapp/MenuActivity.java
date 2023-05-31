package javeriana.edu.co.medicameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javeriana.edu.co.medicameapp.databinding.ActivityDistribucionYreciclajeBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityMainBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityMenuBinding;
import javeriana.edu.co.medicameapp.notifications.NotificacionesBackground;

public class MenuActivity extends AppCompatActivity {

    ActivityMenuBinding binding;

    // Firebase Auth
    private FirebaseAuth mAuth;
    private static final int NOTIFICATIONS_PERMISSION_REQUEST_CODE = 1;

    public static final String PATH_USERS="users/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), StepsActivity.class);
                startActivity(intent);

            }
        });

        binding.profile.setClickable(true);
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(), FotoPerfilActivity.class);
                startActivity(intent);
            }
        });

        binding.layoutReceta.setClickable(true);
        binding.layoutReceta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getBaseContext(), EnvioRecetaMedica.class);
                startActivity(intent);
            }
        });

        binding.layoutDistribicion.setClickable(true);
        binding.layoutDistribicion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getBaseContext(), DistribucionYReciclaje.class);
                startActivity(intent);
            }
        });

        binding.layoutReserva.setClickable(true);
        binding.layoutReserva.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getBaseContext(), ReservaMedicamento.class);
                startActivity(intent);
            }
        });

        binding.layoutDomicilio.setClickable(true);
        binding.layoutDomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DomiciliooActivity.class);
                startActivity(intent);
            }
        });


        // Firebase log out
        binding.logOut.setOnClickListener((view) -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MenuActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        updateNameIDAndEps();
        notificationPermissionAndListenerServiceStart();
    }

    // Notificaciones. Permisos.
    private void notificationPermissionAndListenerServiceStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATIONS_PERMISSION_REQUEST_CODE
            );
        } else {
            // Init service if the device has not been restarted.
            Intent intent = new Intent(this, NotificacionesBackground.class);
            getApplicationContext().startForegroundService(intent);
            Log.i("Notifications", "Valid credentials. Redirecting to the main activity.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATIONS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                notificationPermissionAndListenerServiceStart();
            } else {
                // Permission denied
                new AlertDialog.Builder(this)
                        .setTitle("Permiso de notificación denegado")
                        .setMessage("Debe aceptar el permiso de las notificaciones para poder recibir notificaciones de sus mensajes y pedidos.\n" +
                                "Si denega el permiso muchas veces, deberá activarlo desde la configuración.")
                        .setPositiveButton("Conceder permiso", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Retry
                                notificationPermissionAndListenerServiceStart();
                            }
                        })
                        .setNegativeButton("Ignorar", null)
                        .show();
            }
        }
    }








    // FirebaseAuth Stuff
    @Override
    protected void onStart() {
        super.onStart();

        // Auth Init
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateNameIDAndEps(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(PATH_USERS + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = snapshot.child("id").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String eps = snapshot.child("eps").getValue().toString();
                if(eps.equals("Famisanar")){
                    binding.logoeps.setImageResource(R.drawable.logofamisanar);
                }else if(eps.equals("Compensar")){
                    binding.logoeps.setImageResource(R.drawable.logocompensar);
                }else if(eps.equals("Sanitas")){
                    binding.logoeps.setImageResource(R.drawable.logosanitas);
                }
                else if (eps.equals("Colsanitas")){
                    binding.logoeps.setImageResource(R.drawable.colsanitaslogo);
                }
                binding.headText.setText("Bienvenido, \n"+name+"\n"+"ID "+id);
                binding.afiliado.setText("Entidad Actual:\n"+eps);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Error", "Error al cargar los datos");
            }
        });

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = user.getDisplayName();

        Log.d("REGISTER - AUTH", "setDisplayName:" + displayName);

        // Format xxx xxx xxx;123
        // String[] parts = displayName.split(";");
        // String name = parts[0];
        // String id = parts[1];

        if (displayName != null) {
            String[] parts = displayName.split(";");
            String name = parts[0];
            String id = parts[1];

            // binding.textView2.setText("Bienvenido,\n" + displayName);
            binding.textView2.setText("Bienvenido,\n" + name + "\nID " + id);
        }*/
    }


    // PhotoStuff

    @Override
    protected void onResume() {
        super.onResume();
        updatePhoto();
    }

    public void updatePhoto(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ImageView perfilImageView = binding.profile;
        String imagePath = sharedPreferences.getString("imagePath", "");
        if (!imagePath.isEmpty()) {
            Uri imageUri = Uri.parse(imagePath);
            perfilImageView.setImageURI(imageUri);
        }
    }
}