package com.example.termocoperta;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.termocoperta.ClassDati.Constanti;
import com.example.termocoperta.ClassDati.StatoCoperta;
import com.example.termocoperta.Service.MyCountDownTimer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private static IntentFilter filter;
    private static MyBroadcastNotificationReceiver myBroadcastNotificationReceiver; //Callback Broadcast message receiver

    private static Boolean appAttiva = false;
    private static MyCountDownTimer myCountDownTimer;
    private static AltervistaMyServer altervistaMyServer;

    private static TextView textTimer;
    private static Switch switchCoperta;

    private static AWSiot awSiot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textTimer = (TextView)findViewById(R.id.textTimer);
        switchCoperta = (Switch)findViewById(R.id.switch1);

        int resultCode =  GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) ;
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                Log.d("MAIN", GoogleApiAvailability.getInstance().getErrorString(resultCode));
            }
            else
            {
                Log.d("MAIN", "This device is not supported");
            }
        }
        else
        {
            Log.d("MAIN", "Google Play Services is available.");
        }



        filter = new IntentFilter(Constanti.BROADCAST_ACTION);
        myBroadcastNotificationReceiver = new MyBroadcastNotificationReceiver();

        Log.d("MAIN", "myBroadcastNotificationReceiver = new MyBroadcastNotificationReceiver()");

        myBroadcastNotificationReceiver.registerCallback(new MyBroadcastNotificationReceiver.IMyBroadcastNotificationReceiver() {
            @Override
            public void sendMessage(String message) {
                StatoCoperta statoCoperta = new StatoCoperta();
                try {
                    JSONObject js = new JSONObject(message);
                    statoCoperta.date =  js.getString("date");
                    statoCoperta.time = js.getString("time");
                    statoCoperta.State =  js.getString("State");
                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    try {
                        statoCoperta.datetime = ft.parse(statoCoperta.date + " " + statoCoperta.time);
                    }catch (ParseException pe){}

                    if(statoCoperta.State.toUpperCase().equals("ON")){
                        if(appAttiva){
                            if(myCountDownTimer != null){
                                if(!myCountDownTimer.isRunning) {
                                    myCountDownTimer.UpdateStatoCoperta(statoCoperta);
                                    myCountDownTimer.CancelTimer(false);
                                    myCountDownTimer.start();
                                    switchCoperta.setChecked(true);
                                    Log.d("MAIN", "myBroadcastNotificationReceiver -> [ON] --> ** ACCENDO ** TIMER");
                                    myCountDownTimer.isRunning = true;
                                }
                            }
                        }
                    }else{
                        if(myCountDownTimer != null){
                            if(myCountDownTimer.isRunning) {
                                myCountDownTimer.cancel();
                                myCountDownTimer.CancelTimer(true);
                                textTimer.setText("0:00:00");
                                switchCoperta.setChecked(false);
                                Log.d("MAIN", "altervistaMyServer -> [OFF] --> SPEGNO TIMER");
                                myCountDownTimer.isRunning = false;
                            }
                        }
                    }
                }catch (JSONException je){}
            }
        });

        Log.d("MAIN", "myCountDownTimer = new MyCountDownTimer()");
        myCountDownTimer = new MyCountDownTimer(3600 * 1000, 1000, new MyCountDownTimer.Callback_update() {
            @Override
            public void updateTimer(String time) {
                //Update Textview
                textTimer.setText(time);
            }

            @Override
            public void updateTimerFinish(String time) {
                //Update Textview
                textTimer.setText(time);
                switchCoperta.setChecked(false);
            }
        });


        Log.d("MAIN", "altervistaMyServer = new AltervistaMyServer()");
        altervistaMyServer = new AltervistaMyServer(this);


        awSiot = new AWSiot(this);
        //awSiot.Connetti();
        awSiot.register_callback_connessione(new AWSiot.onConnect() {
            @Override
            public void IsConnect(Boolean stato) {
                if(stato) {
                     runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            switchCoperta.setVisibility(View.VISIBLE);
                        } });
                }else {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            switchCoperta.setVisibility(View.GONE);
                        } });
                }
            }
        });



        switchCoperta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(awSiot.IsConnect){
                        awSiot.publish("rpyTermoCoperta/out","{\"coperta\":\"on\"}");
                    }
                } else {
                    if(awSiot.IsConnect){
                        awSiot.publish("rpyTermoCoperta/out","{\"coperta\":\"off\"}");
                    }
                }
            }
        });
        switchCoperta.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(check_Internet()) {

            altervistaMyServer.Get_Stato_Coperta(new AltervistaMyServer.StateCopertaCallback() {
                @Override
                public void onSuccess(StatoCoperta stato, Boolean error) {
                    if (stato.State.toUpperCase().equals("ON")) {
                        if (myCountDownTimer != null) {
                            if (!myCountDownTimer.isRunning) {
                                myCountDownTimer.UpdateStatoCoperta(stato);
                                myCountDownTimer.CancelTimer(false);
                                myCountDownTimer.start();
                                switchCoperta.setChecked(true);
                                Log.d("MAIN", "altervistaMyServer -> [ON] --> ** ACCENDO ** TIMER");
                                myCountDownTimer.isRunning = true;
                            }
                        }
                    } else {
                        if (myCountDownTimer != null) {
                            if (myCountDownTimer.isRunning) {
                                myCountDownTimer.cancel();
                                myCountDownTimer.CancelTimer(true);
                                textTimer.setText("0:00:00");
                                switchCoperta.setChecked(false);
                                Log.d("MAIN", "altervistaMyServer -> [OFF] --> SPEGNO TIMER");
                                myCountDownTimer.isRunning = false;
                            }
                        }
                    }
                }
            });

            appAttiva = true;
            registerReceiver(myBroadcastNotificationReceiver, filter);
            Log.d("MAIN", "registerReceiver()..........");

            if (!awSiot.IsConnect) {
                awSiot.Connetti();
            }
        }else{
            show_NO_Internet();
            //finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(myBroadcastNotificationReceiver);

        Log.d("MAIN", "myBroadcastNotificationReceiver=null");

        if(awSiot.IsConnect){
            awSiot.disconnect();
        }

        appAttiva = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        appAttiva = false;
    }


    private Boolean check_Internet(){
        //check internet connection
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    private void show_NO_Internet(){
        Toast.makeText(getApplicationContext(), "You are not connected to Internet", Toast.LENGTH_LONG).show();
    }
}

/*
*
*
*
buildscript {
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.3.1' //3.1.4
        classpath 'com.google.gms:google-services:4.0.0' //4.1.0

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
* apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.example.termocoperta"
        minSdkVersion 23
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.google.firebase:firebase-messaging:17.3.2'
    implementation 'com.google.firebase:firebase-core:16.0.1'

    implementation 'com.android.volley:volley:1.1.1'

    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
}
apply plugin: 'com.google.gms.google-services'

*
*
*
*
*
*
*
*
*
*
*
*
*
*
* */