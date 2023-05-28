package javeriana.edu.co.medicameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import javeriana.edu.co.medicameapp.databinding.ActivityRegistroBinding;

public class Registro extends AppCompatActivity {
    ActivityRegistroBinding bindingRegistro;
    // Firebase Auth
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    public static final String PATH_USERS="users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingRegistro = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(bindingRegistro.getRoot());
        database = FirebaseDatabase.getInstance();

        bindingRegistro.iniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    // Firebase Auth Stuff
    @Override
    protected void onStart() {
        super.onStart();

        // Auth Init
        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser() {

        // Los campos se validan antes
        if (validateForm()) {
            mAuth.fetchSignInMethodsForEmail(bindingRegistro.correoInput.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> signInMethods = task.getResult().getSignInMethods();
                    if (signInMethods.isEmpty()) {
                        mAuth.createUserWithEmailAndPassword(bindingRegistro.correoInput.getText().toString(), bindingRegistro.contrasenaInput.getText().toString())
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("REGISTER", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                            user = mAuth.getCurrentUser();
                                            if (user != null) { //Update user Info
                                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();

                                                // Format = XXX;123
                                                String displayName = bindingRegistro.nombreInput.getText().toString() + ";" + bindingRegistro.IdInput.getText().toString();
                                                upcrb.setDisplayName(displayName);

                                                // Normalmente solo va esta linea
                                                //user.updateProfile(upcrb.build());

                                                // Implementado para que no pase a la siguiente actividad hasta que la operacion asincrona termine.
                                                // Se espera hasta que termine la operacion, antes de intentar conseguir el resultado.
                                                user.updateProfile(upcrb.build())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Update successful, retrieve the updated display name
                                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                                    String displayName = user.getDisplayName();
                                                                    Log.d("REGISTER COMMIT", "REGISTER COMMIT DONE. setDisplayName:" + displayName);
                                                                    MyUser myUser=new MyUser();
                                                                    myUser.setName(bindingRegistro.nombreInput.getText().toString());
                                                                    myUser.setId(bindingRegistro.IdInput.getText().toString());
                                                                    myUser.setEPS(bindingRegistro.EPSInput.getText().toString());
                                                                    CustomLatLng customLatLng=new CustomLatLng(0,0);
                                                                    myUser.setLocation(customLatLng);
                                                                    myRef = database.getReference(PATH_USERS+user.getUid());
                                                                    myRef.setValue(myUser);
                                                                    Log.i("Escritura:", "Se escribió en la base de datos");
                                                                    // Now you can update the UI with the updated display name
                                                                    updateUI(user);
                                                                } else {
                                                                    // Update failed, handle the error
                                                                    Log.e("REGISTER COMMIT", "Failed to update display name");
                                                                }
                                                            }
                                                        });


                                                Toast.makeText(Registro.this, "Registro exitoso.",
                                                        Toast.LENGTH_LONG).show();
                                                updateUI(user);

                                                Log.d("REGISTER", "setDisplayName:" + upcrb.getDisplayName());
                                            }
                                        }
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(Registro.this, "Correo en uso. Intentelo de nuevo con otras credenciales de correo",
                                                    Toast.LENGTH_LONG).show();
                                            Log.e("REGISTER", task.getException().getMessage());

                                            // El campo de correo se borra
                                            bindingRegistro.correoInput.setText("");
                                        }
                                    }
                                });

                    } else {
                        // El correo electrónico ya está registrado
                        Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    // Ocurrió un error al verificar si el correo electrónico está registrado
                    Toast.makeText(this, "Error al verificar correo electrónico: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            bindingRegistro.correoInput.setText("");
            bindingRegistro.contrasenaInput.setText("");
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = bindingRegistro.correoInput.getText().toString();
        if (TextUtils.isEmpty(email)) {
            bindingRegistro.correoInput.setError("Se necesita que ingrese su correo.");
            valid = false;
        } else {
            bindingRegistro.correoInput.setError(null);
        }
        String password = bindingRegistro.contrasenaInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            bindingRegistro.contrasenaInput.setError("Se necesita que ingrese su contrasena.");
            valid = false;
        } else {
            bindingRegistro.contrasenaInput.setError(null);
        }

        String id = bindingRegistro.IdInput.getText().toString();
        if (TextUtils.isEmpty(id)) {
            bindingRegistro.IdInput.setError("Se necesita que ingrese su ID nacional.");
            valid = false;
        } else {
            bindingRegistro.IdInput.setError(null);
        }

        String Eps = bindingRegistro.EPSInput.getText().toString();
        if (TextUtils.isEmpty(Eps)) {
            bindingRegistro.EPSInput.setError("Se necesita que ingrese su Eps.");
            valid = false;
        } else {
            bindingRegistro.EPSInput.setError(null);
        }

        return valid;
    }

}