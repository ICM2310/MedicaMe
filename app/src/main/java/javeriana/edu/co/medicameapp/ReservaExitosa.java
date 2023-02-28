package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityReservaExitosaBinding;

public class ReservaExitosa extends AppCompatActivity
{
    ActivityReservaExitosaBinding bindingReservaExitosa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingReservaExitosa = ActivityReservaExitosaBinding.inflate(getLayoutInflater());
        setContentView(bindingReservaExitosa.getRoot());

        bindingReservaExitosa.LinearLayoutMedicamentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.i("ReservaExitosa - onClick", "Flag #1");

                Intent intentIrAlChat = new Intent(getBaseContext(), ChatActivity.class);
                startActivity(intentIrAlChat);
            }
        });
    }
}