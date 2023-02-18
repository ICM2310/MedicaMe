package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityPuntosDistribucionBinding;

public class PuntosDistribucion extends AppCompatActivity {
    ActivityPuntosDistribucionBinding bindingPuntosDistribucion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_puntos_distribucion);
        bindingPuntosDistribucion = ActivityPuntosDistribucionBinding.inflate(getLayoutInflater());
        bindingPuntosDistribucion.bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Boton Back mapa","Se uso el boton de retroceder en la actividad del mapa");
                //TODO implementacion de el boton back para los mapas.
            }
        });
    }
}