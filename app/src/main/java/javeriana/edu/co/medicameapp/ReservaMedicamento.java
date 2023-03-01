package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityMainBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityReservaMedicamentoBinding;

public class ReservaMedicamento extends AppCompatActivity {

    ActivityReservaMedicamentoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_medicamento);
        binding = ActivityReservaMedicamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSolicitarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ReservaExitosa.class);
                startActivity(intent);
            }
        });

        binding.buttonSolicitarDomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DomicilioExitoso.class);
                startActivity(intent);
            }
        });

        binding.backButtonReservaMedicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });


    }
}