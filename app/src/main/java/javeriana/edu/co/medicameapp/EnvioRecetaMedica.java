package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;

public class EnvioRecetaMedica extends AppCompatActivity
{
    EnvioRecetaMedica binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //binding = EnvioRecetaMedica.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        // Para utilizar el font "Roboto"
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Roboto-Regular.ttf");
    }
}