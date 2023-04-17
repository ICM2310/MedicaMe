package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javeriana.edu.co.medicameapp.databinding.ActivityDistribucionYreciclajeBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityMainBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {

    ActivityMenuBinding binding;

    // Firebase Auth
    private FirebaseAuth mAuth;


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
                Intent intent = new Intent(getBaseContext(), DomicilioActivity.class);
                startActivity(intent);
            }
        });


        // Firebase log out
        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(MenuActivity.this, "Sesion cerrada exitosamente.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    // FirebaseAuth Stuff
    @Override
    protected void onStart() {
        super.onStart();

        // Auth Init
        mAuth = FirebaseAuth.getInstance();
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