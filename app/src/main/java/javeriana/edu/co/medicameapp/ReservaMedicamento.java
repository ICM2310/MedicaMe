package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityMainBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityReservaMedicamentoBinding;
import javeriana.edu.co.medicameapp.notifications.notificationsHelper;

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

                // Create notification
                notificationsHelper.showReservaNotification(getBaseContext(), "Reserva exitosa", "Su reserva ha sido recibida por el punto de distribucion.");

                Intent intent = new Intent(getBaseContext(), ReservaExitosaa.class);
                startActivity(intent);
            }
        });

        binding.buttonSolicitarDomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create notification
                notificationsHelper.showDomicilioNotification(getBaseContext(), "Su solicitud de domicilio ha sido recibida", "Esperamos asignarle un repartidor pronto");


                Intent intent = new Intent(getBaseContext(), DomicilioExitosoMap.class);
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