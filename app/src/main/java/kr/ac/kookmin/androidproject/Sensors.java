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

public class Sensors {

    private class Accelerometer accelerometer = new Acceleromter();
    private class Gyroscope gyroscope = new Gyroscope();
    private class Magnetometer magnetometer = new Magnetometer();
    }

        
