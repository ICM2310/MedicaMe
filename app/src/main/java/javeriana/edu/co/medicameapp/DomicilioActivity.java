package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityDomicilioBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityMenuBinding;

public class DomicilioActivity extends AppCompatActivity {

    ActivityDomicilioBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domicilio);
        binding = ActivityDomicilioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSolicitarDomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DomicilioExitoso.class);
                startActivity(intent);
            }
        });
    }
}