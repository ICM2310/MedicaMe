package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityDistribucionYreciclajeBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityPuntosReciclajeBinding;

public class PuntosReciclajeActivity extends AppCompatActivity {
    ActivityPuntosReciclajeBinding bindingPuntosReciclaje ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_puntos_reciclaje);
        bindingPuntosReciclaje = ActivityPuntosReciclajeBinding.inflate(getLayoutInflater());
        setContentView(bindingPuntosReciclaje.getRoot());
        bindingPuntosReciclaje.bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Boton Back mapa","Se uso el boton de retroceder de puntos de reciclaje en la actividad del mapa");
                Intent retornar = new Intent(getBaseContext(), DistribucionYReciclaje.class);
                startActivity(retornar);
            }
            
        });


    }
}