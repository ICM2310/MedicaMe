package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import javeriana.edu.co.medicameapp.databinding.ActivityAutenticacionBinding;

public class Autenticacion extends AppCompatActivity
{
    ActivityAutenticacionBinding bindingAutenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingAutenticacion = ActivityAutenticacionBinding.inflate(getLayoutInflater());
        setContentView(bindingAutenticacion.getRoot());


        //Recibir credenciales
        bindingAutenticacion.iniciarSesionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String correoRecibido = bindingAutenticacion.correoInput.getText().toString();
                String contrasenaRecibida = bindingAutenticacion.contrasenaInput.getText().toString();

                Log.i("Correo electronico", correoRecibido);
                Log.i("Contrasena", contrasenaRecibida);

                // Llamar a la otra interfaz
                Intent pasarAMenu = new Intent(getBaseContext(), MenuActivity.class);
                // Arrancarla
                startActivity(pasarAMenu);
            }
        });

        // Registrar nuevo usuario
        bindingAutenticacion.Registrarse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("Test", "Se pasa a registrar el usuario");

                // Llamar a la otra interfaz
                Intent pasarARegistro = new Intent(getBaseContext(), Registro.class);
                // Arrancarla
                startActivity(pasarARegistro);
            }
        });
    }
}