package javeriana.edu.co.medicameapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javeriana.edu.co.medicameapp.databinding.ActivityEnvioRecetaMedicaBinding;

public class EnvioRecetaMedica extends AppCompatActivity
{
    ActivityEnvioRecetaMedicaBinding bindingEnvioRecetaMedica;
    Uri uriCamara;

    ActivityResultLauncher<String> galleryRequest = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> loadImage(result)
    );

    ActivityResultLauncher<Uri> cameraRequest = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> loadImage(uriCamara)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingEnvioRecetaMedica = ActivityEnvioRecetaMedicaBinding.inflate(getLayoutInflater());
        setContentView(bindingEnvioRecetaMedica.getRoot());
        Log.i("Carga de pantalla: ", "Completada");

        bindingEnvioRecetaMedica.backButtonEnvioRecetaMedica.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("Entrada al boton de back ", "Entró");
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

        initializeFile();
        bindingEnvioRecetaMedica.fromGalleryButton.setOnClickListener(view -> galleryRequest.launch("image/*"));
        bindingEnvioRecetaMedica.fromCameraButton.setOnClickListener(view -> cameraRequest.launch(uriCamara));
    }

    private void loadImage(Uri result) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(result);
            Bitmap image = BitmapFactory.decodeStream(imageStream);
            Intent intent = new Intent(this, ConfirmacionEnvioRecetaActivity.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("bitmap", byteArray);
            startActivity(intent);
            // Inicia la otra actividad
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeFile() {
        File file = new File(getFilesDir(), "picFromCamera");
        uriCamara = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
    }
}