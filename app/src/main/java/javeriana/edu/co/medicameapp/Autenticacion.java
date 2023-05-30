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
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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


        // If the user was logged in... get it to the menu
        // onStartInit();
    }





    // Firebase Auth Stuff
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Automatically sign in if user is authenticated.
        Log.i("updateUI", "Entered:success");
        // updateUI(currentUser);
    }

    public void onStartInit(){
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Automatically sign in if user is authenticated.
        Log.i("updateUI", "Entered:success");
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Comprobar si el usuario es una farmacia
            DatabaseReference pharmaciesRef = databaseReference.child("pharmacies/").child(userId);
            pharmaciesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // El usuario es una farmacia, redirigir a la pantalla de farmacia
                        Intent intent = new Intent(getBaseContext(), FarmaciaActivity.class);
                        startActivity(intent);
                    } else {
                        // Comprobar si el usuario es un repartidor
                        DatabaseReference deliveriesRef = databaseReference.child("deliveries/").child(userId);
                        deliveriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // El usuario es un repartidor, redirigir a la pantalla de repartidor
                                    Intent intent = new Intent(getBaseContext(), RepartidorActivity.class);
                                    startActivity(intent);
                                } else {
                                    // El usuario es un usuario normal, redirigir a la pantalla de usuario
                                    Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Error al obtener la información de repartidor
                            }

                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error al obtener la información de farmacia
                }
            });
        } else {
            bindingAutenticacion.correoInput.setText("");
            bindingAutenticacion.contrasenaInput.setText("");
            Log.i("else", "Entered:success");
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

                                // Escribir el archivo con los datos de AUTH
                                writeJSONAuth();

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

    // Se sobreescribe el archivo para guardar la info del ultimo log in
    public void writeJSONAuth() {
        String email = bindingAutenticacion.correoInput.getText().toString();
        String password = bindingAutenticacion.contrasenaInput.getText().toString();
        Writer output = null;
        String filename = "login_credentials.json";
        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            Log.i("LOCATION", "Ubicacion de archivo: " + file);
            output = new BufferedWriter(new FileWriter(file, false)); // Use NO append mode, with false. (OVERWRITE)
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            output.write(jsonObject.toString() + ",\n"); // Append the new entry to the existing file
            output.close();
            Toast.makeText(getApplicationContext(), "Credentials saved", Toast.LENGTH_LONG).show();
            Log.i("CREDENTIALS_SAVED. JSON... ", jsonObject.toString());
        } catch (Exception e) {
            //Log error
        }
    }

    // Lee el archivo.
    public LoginCredentials readJSONCredentials() throws JSONException {
        LoginCredentials credentials = null;

        String filename = "login_credentials.json";
        File file = new File(getBaseContext().getExternalFilesDir(null), filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("{") && line.endsWith("},")) {
                    line = line.substring(1, line.length() - 2); // Remove leading and trailing braces and comma
                    String[] parts = line.split(",");
                    String email = null;
                    String password = null;
                    for (String part : parts) {
                        String[] keyValue = part.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim().replaceAll("\"", "");
                            String value = keyValue[1].trim().replaceAll("\"", "");
                            if (key.equals("email")) {
                                email = value;
                            } else if (key.equals("password")) {
                                password = value;
                            }
                        }
                    }
                    if (email != null && password != null) {
                        credentials = new LoginCredentials(email, password);
                        break; // Stop reading the file once a credential is found
                    }
                }
            }

            if (credentials != null) {
                Log.i("JSON FILE READ - CREDENTIAL.", "The JSON FILE WITH CREDENTIAL INFO HAS BEEN READ");
                Log.i("JSON FILE READ - CREDENTIAL.", "Credential from file: " + credentials);
                System.out.println("Credential from file: " + credentials);
            } else {
                Log.i("JSON FILE READ - CREDENTIAL.", "No credentials found in the JSON file");
            }

        } catch (FileNotFoundException e) {
            Log.i("JSON FILE READ ERROR - CREDENTIAL.", "The JSON FILE WITH CREDENTIAL INFO COULD NOT BE READ");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.i("JSON FILE READ ERROR - CREDENTIAL.", "The JSON FILE WITH CREDENTIAL INFO COULD NOT BE READ");
            throw new RuntimeException(e);
        }

        return credentials;
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

            LoginCredentials credentials = null;
            try {
                credentials = readJSONCredentials();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            if (credentials != null) {
                // Se procede a leer el archivo con los datos de la ultima Auth.
                String correoRecibido = credentials.getEmail();
                String contrasenaRecibida = credentials.getPassword();

                Log.i("Correo electronico del archivo = ", correoRecibido);
                Log.i("Contrasena del archivo = ", contrasenaRecibida);

                notifyUser("Authentication Success!");

                bindingAutenticacion.correoInput.setText(correoRecibido);
                bindingAutenticacion.contrasenaInput.setText(contrasenaRecibida);

                signInUser(bindingAutenticacion.correoInput.getText().toString(), bindingAutenticacion.contrasenaInput.getText().toString());
            }
            else{
                // Si no habian credenciales en el archivo, avisar.
                Toast.makeText(getBaseContext(), "No se han detectado sesiones previas. Ingrese sus datos", Toast.LENGTH_LONG).show();
            }

            // startActivity(new Intent(getBaseContext(), MenuActivity.class));
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