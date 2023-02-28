package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityDomicilioExitosoBinding;

public class DomicilioExitoso extends AppCompatActivity
{
    ActivityDomicilioExitosoBinding bindingDomicilioExitoso;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingDomicilioExitoso = ActivityDomicilioExitosoBinding.inflate(getLayoutInflater());
        setContentView(bindingDomicilioExitoso.getRoot());


        bindingDomicilioExitoso.LinearLayoutMedicamentos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("DomicilioExitoso - onClick", "Flag #1");
            }
        });
    }
}