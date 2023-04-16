package javeriana.edu.co.medicameapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javeriana.edu.co.medicameapp.databinding.ActivityFotoPerfilBinding;

public class FotoPerfilActivity extends AppCompatActivity {
    private ActivityFotoPerfilBinding binding;
    Uri uriCamera;
    SharedPreferences sharedPreferences;

    ActivityResultLauncher<String> galleryRequest = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> loadImage(result)
    );
    ActivityResultLauncher<Uri> cameraRequest = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> loadImage(uriCamera)
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFotoPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imagePath=sharedPreferences.getString("imagePath", "");
        if(!imagePath.isEmpty()){
            Uri imageUri = Uri.parse(imagePath);
            binding.fotoPerfilPersona.setImageURI(imageUri);
        }
        initializeFile();
        binding.cambioFotoGaleriaButton.setOnClickListener(view -> galleryRequest.launch("image/*"));
        binding.cambioFotoCamaraButton.setOnClickListener(view -> cameraRequest.launch(uriCamera));



    }
    private void loadImage(Uri result) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(result);
            Bitmap image = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String imagePath = saveImageToInternalStorage(byteArray);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("imagePath", imagePath);
            editor.apply();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeFile() {
        File file = new File(getFilesDir(), "picFromCamera");
        uriCamera = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
    }

    private String saveImageToInternalStorage(byte[] byteArray) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        try {
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(byteArray);
            fos.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}