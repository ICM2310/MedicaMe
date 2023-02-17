package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
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

        // Evitar que la pantalla rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Para que la app corra en portrait mode
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Para que la app corra en landscape mode

        bindingEnvioRecetaMedica.backButtonEnvioRecetaMedica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.i("Entrada al boton de back ", "Entró");
            }
        });

        bindingEnvioRecetaMedica.fromGalleryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("Entrada al boton de galeria", "Entró");
            }
        });

        bindingEnvioRecetaMedica.fromCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("Entrada al boton de camara", "Entró");
            }
        });
    }
}