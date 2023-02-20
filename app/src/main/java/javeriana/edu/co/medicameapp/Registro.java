package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import javeriana.edu.co.medicameapp.databinding.ActivityRegistroBinding;

public class Registro extends AppCompatActivity
{
    ActivityRegistroBinding bindingRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingRegistro = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(bindingRegistro.getRoot());
    }
}