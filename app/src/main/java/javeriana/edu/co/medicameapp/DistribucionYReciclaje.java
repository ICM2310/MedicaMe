package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

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
    }
}