
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

public class Accelerometer {

    private double xvalue;
    private double yvalue;
    private double zvalue;

    @Override onSensorChanged(SensorEvent event) {
        xvalue = event.values[0];
        yvalue = event.values[1];
        zvalue = event.values[2];
    }
}
