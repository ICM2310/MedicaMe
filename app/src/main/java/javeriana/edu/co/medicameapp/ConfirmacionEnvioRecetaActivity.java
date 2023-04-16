package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import javeriana.edu.co.medicameapp.databinding.ActivityConfirmacionEnvioRecetaBinding;
import javeriana.edu.co.medicameapp.databinding.ActivityEnvioRecetaMedicaBinding;

public class ConfirmacionEnvioRecetaActivity extends AppCompatActivity {

    private ActivityConfirmacionEnvioRecetaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityConfirmacionEnvioRecetaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtiene el arreglo de bytes del Intent
        byte[] byteArray = getIntent().getByteArrayExtra("bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        binding.receta.setImageBitmap(bitmap);

        binding.backButtonEnvioRecetaMedica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v("Boton Back receta medica","Se uso el boton de retroceder de confirmación de receta medica");
                finish();
            }
        });

    }
}