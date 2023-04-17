package javeriana.edu.co.medicameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import javeriana.edu.co.medicameapp.databinding.ActivityStepsBinding;

public class StepsActivity extends AppCompatActivity implements SensorEventListener {
    private static final int SENSOR_PERMISSION_CODE = 1;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor, gyroscopeSensor;
    private long lastTimestamp = 0;
    private float[] accelerometerReading = new float[3];
    private float[] gyroscopeReading = new float[3];
    private TextView stepCounterTextView;

    private float lastPitch = 0f;
    private boolean isStepCountingStarted = false;
    private int stepCount = 0;


    ActivityStepsBinding bindingSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("entro", "entro a la actividad steps");
        bindingSteps = ActivityStepsBinding.inflate(getLayoutInflater());
        setContentView(bindingSteps.getRoot());
        stepCounterTextView = bindingSteps.stepCounterTextView;
        initializeSensors();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerReading = event.values.clone();
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeReading = event.values.clone();
                break;
        }
        calculateStepCount();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    private void calculateStepCount() {
        final float STEP_THRESHOLD = 10f;
        final float ALPHA = 0.98f;//+ ->Menos Sensible - -> Mas sensible

        float[] rotationMatrix = new float[9];
        float[] orientationAngles = new float[3];
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, gyroscopeReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // Apply low pass filter to smooth out the pitch value
        float pitch = orientationAngles[1] * 180 / (float) Math.PI;
        pitch = ALPHA * pitch + (1 - ALPHA) * lastPitch;
        lastPitch = pitch;

        if (pitch > STEP_THRESHOLD && !isStepCountingStarted) {
            isStepCountingStarted = true;
            stepCount++;
        } else if (pitch < STEP_THRESHOLD && isStepCountingStarted) {
            isStepCountingStarted = false;
        }

        // Update the step count TextView
        bindingSteps.stepCounterTextView.setText(String.valueOf(stepCount));
    }
    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (accelerometerSensor == null || gyroscopeSensor == null) {
            Toast.makeText(this, "This device does not have the required sensors.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

