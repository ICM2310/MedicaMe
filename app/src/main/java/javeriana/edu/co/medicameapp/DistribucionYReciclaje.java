package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityDistribucionYreciclajeBinding;

public class DistribucionYReciclaje extends AppCompatActivity
{
    ActivityDistribucionYreciclajeBinding bindingDistribucionYReciclaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingDistribucionYReciclaje = ActivityDistribucionYreciclajeBinding.inflate(getLayoutInflater());
        setContentView(bindingDistribucionYReciclaje.getRoot());

        // Evitar que la pantalla rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Para que la app corra en portrait mode

        bindingDistribucionYReciclaje.backButtonDistribucionYReciclaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.i("Funcionamiento de botones", "Boton de back");
            }
        });

        bindingDistribucionYReciclaje.puntosDeDistribucionDistribucionYReciclaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.i("Funcionamiento de botones", "Boton de puntos de distribucion");
                Intent intent = new Intent(getBaseContext(), PuntosDistribucion.class);
                startActivity(intent);
            }
        });

        bindingDistribucionYReciclaje.puntosDeReciclajeDistribucionYReciclaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.i("Funcionamiento de botones", "Boton de puntos de reciclaje");
                Intent intent = new Intent(getBaseContext(), PuntosReciclajeActivity.class);
                startActivity(intent);
            }
        });
    }
}