package javeriana.edu.co.medicameapp;

import static java.lang.Math.sqrt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javeriana.edu.co.medicameapp.databinding.ActivityStepsBinding;

public class StepsActivity extends AppCompatActivity implements SensorEventListener {
    ActivityStepsBinding bindingSteps;
    private SensorManager sensorManager;
    private Sensor accelerometer;


    private int stepCount = 0;



    private final long TIME_THRESHOLD_NS = 2000000000L; // 2 seconds
    private final float MAGNITUDE_THRESHOLD = 2.0f; // adjust as needed
    private final int WINDOW_SIZE = 20; // number of sensor events to store in the buffer
    /*
    TIME_THRESHOLD_NS: This is the time threshold in nanoseconds.
    It specifies the minimum time that must elapse between two steps to count them as separate steps.
    In this case, the time threshold is set to 2 seconds (2000000000 nanoseconds),
    meaning that two consecutive steps that occur within 2 seconds of each other will be counted as a single step.

    MAGNITUDE_THRESHOLD: This is the threshold for the magnitude of the acceleration or gyroscope vector.
    If the magnitude of the vector exceeds this threshold, it is considered a step.
    The value of this threshold is set to 2.0f, but it can be adjusted as needed depending on the specific sensor and device being used.

    WINDOW_SIZE: This is the number of sensor events to store in the buffer.
    The buffer is used to calculate the average magnitude of the acceleration or gyroscope vector over a certain period of time.
    In this case, the window size is set to 20, meaning that the buffer will store the last 20 sensor events.

     */

    private List<Float> accelerationBuffer = new ArrayList<>();
    private List<Float> gyroscopeBuffer = new ArrayList<>();
    private long lastStepTimestamp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("entro","entro a la actividad steps");
        bindingSteps = ActivityStepsBinding.inflate(getLayoutInflater());
        setContentView(bindingSteps.getRoot());
        TextView stepCounterTextView = bindingSteps.stepsText;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bindingSteps.stepsText.setText("hola");

        //bindingSteps.textView.setText(stepCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Calculate the magnitude of the acceleration vector
            float acceleration = (float) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);

            accelerationBuffer.add(acceleration);

            // If the buffer is full, remove the oldest event
            if (accelerationBuffer.size() > WINDOW_SIZE) {
                accelerationBuffer.remove(0);
            }

            // Check if the magnitude of the acceleration vector has crossed a threshold
            if (acceleration > MAGNITUDE_THRESHOLD) {
                // Check the time since the last step
                long timestamp = event.timestamp;
                long timeDifference = timestamp - lastStepTimestamp;

                if (timeDifference > TIME_THRESHOLD_NS) {
                    // Count the step
                    lastStepTimestamp = timestamp;
                    stepCount++;
                    bindingSteps.stepsText.setText("Steps taken: " + stepCount);
                }
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Calculate the magnitude of the gyroscope vector
            float gyroscope = (float) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);

            gyroscopeBuffer.add(gyroscope);

            // If the buffer is full, remove the oldest event
            if (gyroscopeBuffer.size() > WINDOW_SIZE) {
                gyroscopeBuffer.remove(0);
            }

            // Check if the magnitude of the gyroscope vector has crossed a threshold
            if (gyroscope > MAGNITUDE_THRESHOLD) {
                // Check the time since the last step
                long timestamp = event.timestamp;
                long timeDifference = timestamp - lastStepTimestamp;

                if (timeDifference > TIME_THRESHOLD_NS) {
                    // Count the step
                    lastStepTimestamp = timestamp;
                    stepCount++;
                    bindingSteps.stepsText.setText("Steps taken: " + stepCount);
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}