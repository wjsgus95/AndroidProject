package kr.ac.kookmin.androidproject;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.*;
import java.io.*;
import android.hardware.*;

public class MainActivity extends AppCompatActivity {
    //class

    private TextView imustateText;

    private TextView accarText, accirText; //Average Rate And Instant Rate Text
    private TextView gyroarText, gyroirText;
    private TextView magnetoarText, magnetoirText;

    private TextView accelerometerText, gyroscopeText, magnetometerText;

    private Button leftButton, rightButton;

    private SensorManager SM;

    private Sensor accSensor;
    private Sensor gyroSensor;
    private Sensor magnetoSensor;

    private SensorEventListener accL;
    private SensorEventListener gyroL;
    private SensorEventListener magnetoL;

    private Timer myTimer;
    TimerTask getRate;

    //members
    private boolean startenable = true;
    private boolean stopenable = false;
    private boolean isPause;
    private boolean listenerstate = false;

    private String imuValue;
    private String currentTime;
    private String ACCLOG;
    private String GYROLOG;
    private String MAGNETOLOG;

    private long startTime = 0;

    private long accTotalCount = 0;
    private long accInstantCount = 0;
    private long gyroTotalCount = 0;
    private long gyroInstantCount = 0;
    private long magnetoInstantCount = 0;
    private long magnetoTotalCount = 0;
    

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
        gyroSensor = SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        magnetoSensor = SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        accL = new accListener();
        gyroL = new gyroListener();
        magnetoL = new magnetoListener();

        // Assign TextView

        imustateText = (TextView) findViewById(R.id.imustateText);

        accarText = (TextView) findViewById(R.id.accarText);
        accirText = (TextView) findViewById(R.id.accirText);

        gyroarText = (TextView) findViewById(R.id.gyroarText);
        gyroirText = (TextView) findViewById(R.id.gyroirText);

        magnetoarText = (TextView) findViewById(R.id.magnetoarText);
        magnetoirText = (TextView) findViewById(R.id.magnetoirText);

        accelerometerText = (TextView) findViewById(R.id.accelerometerText);
        gyroscopeText = (TextView) findViewById(R.id.gyroscopeText);
        magnetometerText = (TextView) findViewById(R.id.magnetometerText);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);

        accelerometerText.setText("Accelerometer Response Rate");
        gyroscopeText.setText("Gyroscope Response Rate");
        magnetometerText.setText("Magnetometer Response Rate");

        leftButton.setText("Start");
        rightButton.setText("Stop");

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLeftClick();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRightClick();
            }
        });

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 500);
    }

    private void TimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            accarText.setText("Average Rate : " + (accTotalCount * 1000 / (SystemClock.uptimeMillis() - startTime)) + " times / s");
            gyroarText.setText("Average Rate : " + (gyroTotalCount * 1000 / (SystemClock.uptimeMillis() - startTime)) + " times / s");
            magnetoarText.setText("Average Rate : " + (magnetoTotalCount * 1000 / (SystemClock.uptimeMillis() - startTime)) + " times / s");

            accirText.setText("Instant Rate : " + accInstantCount / 0.5 + " times / s");
            gyroirText.setText("Instant Rate : " + gyroInstantCount / 0.5 + " times / s");
            magnetoirText.setText("Instant Rate : " + magnetoInstantCount / 0.5 + " times / s");

            accInstantCount = 0;
            gyroInstantCount = 0;
            magnetoInstantCount = 0;
        }
    };


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
        listenerOn(SM);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerOff(SM);
    }

    private class accListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (listenerstate) {
                saveLog(event, ACCLOG);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


    private class gyroListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (listenerstate) {
                saveLog(event, GYROLOG); // Save log whenever sensored
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

    private class magnetoListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(listenerstate) {
                saveLog(event, MAGNETOLOG);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private void saveLogFormat(String FILENAME) {
        try {
            File file = new File(getExternalFilesDir(null), FILENAME);

            String Parameter = new String();
            String Unit = new String();

            if (FILENAME == ACCLOG) {
                Parameter = "Time,AX,AY,AZ\n";
                Unit = "ms,m/s^2,m/s^2,m/s^2\n";
            }

            if (FILENAME == GYROLOG) {
                Parameter = "Time,GX,GY,GZ\n";
                Unit = "ms,rad/s,rad/s,rad/s\n";
            }

            if (FILENAME == MAGNETOLOG) {
                Parameter = "Time,MX,MY,MZ\n";
                Unit = "ms,uT,uT,uT\n";
            }

            OutputStream logOut = new FileOutputStream(file, true);
            logOut.write(Parameter.getBytes());
            logOut.write(Unit.getBytes());
            logOut.close();
        } catch (Exception e) {

        }
    }

    private void saveLog(SensorEvent event, String FILENAME) {
        try {
            File file = new File(getExternalFilesDir(null), FILENAME);
            imuValue = event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n";
            currentTime = String.valueOf(SystemClock.uptimeMillis() - startTime) + ",";

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accTotalCount++;
                accInstantCount++;
            }

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
                gyroTotalCount++;
                gyroInstantCount++;
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magnetoTotalCount++;
                magnetoInstantCount++;
            }

            OutputStream logOut = new FileOutputStream(file, true);
            logOut.write(currentTime.getBytes());
            logOut.write(imuValue.getBytes());
            logOut.close();
        } catch (Exception e) {

        }
    }

    private void onLeftClick() {
        if (startenable) {
            stopenable = true;
            leftButton.setText("Pause");
            listenerOn(SM);

            isPause = true;
            startenable = false;

            ACCLOG = new String("A" + setFileName());
            GYROLOG = new String("G" + setFileName());
            MAGNETOLOG = new String("M" + setFileName());

            saveLogFormat(ACCLOG);
            saveLogFormat(GYROLOG);
            saveLogFormat(MAGNETOLOG);

            startTime = SystemClock.uptimeMillis();
        }
        else {
            if (isPause) {
                leftButton.setText("Resume");
                listenerOff(SM);
                isPause = false;
            } else {
                leftButton.setText("Pause");
                listenerOn(SM);
                isPause = true;
            }
        }
    }

    private void onRightClick() {
        if (stopenable) {
            startenable = true;
            leftButton.setText("Start");
            stopenable = false;
            listenerOff(SM);
            accTotalCount = 0;
            gyroTotalCount = 0;
            magnetoTotalCount = 0;
        }
    }

    private void listenerOn(SensorManager SM) {
        SM.registerListener(accL, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(gyroL, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(magnetoL, magnetoSensor, SensorManager.SENSOR_DELAY_FASTEST);
        listenerstate = true;
        imustateText.setText("Sensors Active");
    }

    private void listenerOff(SensorManager SM) {
        SM.unregisterListener(accL);
        SM.unregisterListener(gyroL);
        SM.unregisterListener(magnetoL);
        listenerstate = false;
        imustateText.setText("Sensors Idle");
    }

    private String setFileName() {

         //Gregorian Date and Time Instacne Declaration
        Calendar c = Calendar.getInstance();

        String LOG = "";

        LOG += c.get(Calendar.YEAR);

        if (c.get(Calendar.MONTH) < 10)
            LOG += "0";
        LOG += c.get(Calendar.MONTH) + 1;

        if (c.get(Calendar.DATE) < 10)
            LOG += "0";
        LOG += c.get(Calendar.DATE);

        if (c.get(Calendar.HOUR_OF_DAY) < 10)
            LOG += "0";
        LOG += c.get(Calendar.HOUR_OF_DAY);

        if (c.get(Calendar.MINUTE) < 10)
            LOG += "0";
        LOG += c.get(Calendar.MINUTE);

        LOG += ".csv";

        return LOG;
    }
}
