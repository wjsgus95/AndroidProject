package kr.ac.kookmin.androidproject;


import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;
import java.io.*;


public class MainActivity extends AppCompatActivity {
    //class
    private TextView accxText, accyText, acczText;
    private TextView gyroxText, gyroyText, gyrozText;

    private TextView accstateText, gyrostateText;

    private Button leftButton, midButton, rightButton;

    private SensorManager SM;

    private Sensor accSensor;
    private Sensor gyroSensor;

    private SensorEventListener accL;
    private SensorEventListener gyroL;

    //member
    private boolean listenerstate = false;
    private int buttonstate = 0;

    private String ACCLOG;
    private String GYROLOG;


    //Gregorian Date and Time Instacne Declaration
    Calendar c = Calendar.getInstance();


    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer Sensor

        accSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accL = new accListener();
        gyroL = new gyroListener();

        // Assign TextView
        accxText = (TextView) findViewById(R.id.accxText);
        accyText = (TextView) findViewById(R.id.accyText);
        acczText = (TextView) findViewById(R.id.acczText);

        gyroxText = (TextView) findViewById(R.id.gyroxText);
        gyroyText = (TextView) findViewById(R.id.gyroyText);
        gyrozText = (TextView) findViewById(R.id.gyrozText);

        accstateText = (TextView) findViewById(R.id.accstateText);
        gyrostateText = (TextView) findViewById(R.id.gyrostateText);

        leftButton = (Button) findViewById(R.id.leftButton);
        midButton = (Button) findViewById(R.id.midButton);
        rightButton = (Button) findViewById(R.id.rightButton);

        leftButton.setText("Start");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Resister sensor Listener
        SM.registerListener(accL, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(gyroL, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(accL);
        SM.unregisterListener(gyroL);
    }

    private class accListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (listenerstate == true) {
                accxText.setText("AX " + event.values[0]);
                accyText.setText("AY " + event.values[1]);
                acczText.setText("AZ " + event.values[2]);
                saveLog(event, ACCLOG); // Save log whenever sensored
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


    private class gyroListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (listenerstate == true) {
                gyroxText.setText("GX " + event.values[0]);
                gyroyText.setText("GY " + event.values[1]);
                gyrozText.setText("GZ " + event.values[2]);
                saveLog(event, GYROLOG); // Save log whenever sensored
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

    private void saveLog(SensorEvent event, String FILENAME) {
        try {
            Sensor sensor = event.sensor;
            File file = new File(getExternalFilesDir(null), FILENAME);
            String imuValue = event.values[0] + ":" + event.values[1] + ":" + event.values[2];

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                imuValue = imuValue + "A\n";
                accstateText.setText("Accelerometer Logging...");
            }

            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                imuValue = imuValue + "G\n";
                gyrostateText.setText("Gyroscope Logging...");
            }

            OutputStream logOut = new FileOutputStream(file, true);
            logOut.write(imuValue.getBytes());
            logOut.close();
        } catch (Exception e) {

        }
    }

    private void onLeftClick() {
        switch(buttonstate) {
            case 0 :
                leftButton.setText("Pause");
                ACCLOG = "A" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DATE);
                GYROLOG = "G" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DATE);
                buttonstate = 1;
                listenerOff(SM);
                break;
            case 1 :
                leftButton.setText("Resume");
                listenerOn(SM);
                break;
        }
    }

    private void listenerOn(SensorManager SM) {
        SM.registerListener(accL, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(gyroL, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        listenerstate = true;
    }

    private void listenerOff(SensorManager SM) {
        SM.unregisterListener(accL);
        SM.unregisterListener(gyroL);
        listenerstate = false;
    }

    private String putIndex() {
        return "";
    }
}
