package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import javeriana.edu.co.medicameapp.databinding.ActivityEnvioRecetaMedicaBinding;

public class EnvioRecetaMedica extends AppCompatActivity
{
    ActivityEnvioRecetaMedicaBinding bindingEnvioRecetaMedica;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingEnvioRecetaMedica = ActivityEnvioRecetaMedicaBinding.inflate(getLayoutInflater());
        setContentView(bindingEnvioRecetaMedica.getRoot());
        Log.i("Carga de pantalla: ", "Completada");

        bindingEnvioRecetaMedica.fromGalleryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getBaseContext(), "Desde la galeria", Toast.LENGTH_LONG);
            }
        });

        bindingEnvioRecetaMedica.fromCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getBaseContext(), "Desde la camara", Toast.LENGTH_LONG);
            }
        });
    }
}