package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.BotonUnete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("Test", "Se pasa a verificar credenciales");

                // Llamar a la otra interfaz
                Intent pasarAAutenticacion = new Intent(getBaseContext(), Autenticacion.class);
                // Arrancarla
                startActivity(pasarAAutenticacion);
            }
        });
    }
}