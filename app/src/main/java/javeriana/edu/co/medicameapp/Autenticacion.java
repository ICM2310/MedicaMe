package javeriana.edu.co.medicameapp;

import androidx.annotation.NonNull;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import javeriana.edu.co.medicameapp.databinding.ActivityAutenticacionBinding;

public class Autenticacion extends AppCompatActivity
{
    // Fingerprint Auth
    private CancellationSignal cancellationSignal = null;
    //private BiometricPrompt.AuthenticationCallback authenticationCallback;


    // Firebase Auth
    private FirebaseAuth mAuth;


    ActivityAutenticacionBinding bindingAutenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingAutenticacion = ActivityAutenticacionBinding.inflate(getLayoutInflater());
        setContentView(bindingAutenticacion.getRoot());


        // Firebase Auth init
        mAuth = FirebaseAuth.getInstance();

        // Auth listener
        bindingAutenticacion.iniciarSesionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String correoRecibido = bindingAutenticacion.correoInput.getText().toString();
                String contrasenaRecibida = bindingAutenticacion.contrasenaInput.getText().toString();

                Log.i("Correo electronico", correoRecibido);
                Log.i("Contrasena", contrasenaRecibida);

                signInUser(bindingAutenticacion.correoInput.getText().toString(), bindingAutenticacion.contrasenaInput.getText().toString());

                // Llamar a la otra interfaz
                // Intent pasarAMenu = new Intent(getBaseContext(), MenuActivity.class);
                // Arrancarla
                // startActivity(pasarAMenu);
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

        // Fingreprint listener
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





    // Firebase Auth Stuff
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Automatically sign in if user is authenticated.
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            bindingAutenticacion.correoInput.setText("");
            bindingAutenticacion.contrasenaInput.setText("");
        }
    }

    private void signInUser(String email, String password) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI
                                Log.d("Auth", "signInWithEmail:success");
                                Toast.makeText(Autenticacion.this, "Autenticacion exitosa.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Auth", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Autenticacion.this, "La autenticacion fallo. Revise sus credenciales",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = bindingAutenticacion.correoInput.getText().toString();
        if (TextUtils.isEmpty(email)) {
            bindingAutenticacion.correoInput.setError("Se necesita que ingrese su correo.");
            valid = false;
        } else {
            bindingAutenticacion.correoInput.setError(null);
        }
        String password = bindingAutenticacion.contrasenaInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            bindingAutenticacion.contrasenaInput.setError("Se necesita que ingrese su contrasena.");
            valid = false;
        } else {
            bindingAutenticacion.contrasenaInput.setError(null);
        }
        return valid;
    }





    // Fingerprint Auth stuff
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