package javeriana.edu.co.medicameapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.Executor;

import javeriana.edu.co.medicameapp.databinding.ActivityAutenticacionBinding;

public class Autenticacion extends AppCompatActivity
{
    private CancellationSignal cancellationSignal = null;
    //private BiometricPrompt.AuthenticationCallback authenticationCallback;
    ActivityAutenticacionBinding bindingAutenticacion;

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            notifyUser("Authentication error: " + errString);
        }

        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            notifyUser("Authentication Success!");
            startActivity(new Intent(getBaseContext(), MenuActivity.class));
        }
    };

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
        Executor mainExecutor = ContextCompat.getMainExecutor(this);

        bindingAutenticacion.fingerPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(getBaseContext())
                        .setTitle("Fingerprint Authentication Access")
                        .setSubtitle("Authentication is required")
                        .setDescription("Fingerprint Authentication")
                        .setNegativeButton("Cancel", mainExecutor, (dialog, which) -> {})
                        .build();
                biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback);
            }
        });
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {
            notifyUser("Authentication was cancelled by the user");
        });
        return cancellationSignal;
    }

    private boolean checkBiometricSupport() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {
            notifyUser("Fingerprint has not been enabled in settings.");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint has not been enabled in settings.");
            return false;
        }
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    }


}