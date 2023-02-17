package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;

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
    }
}