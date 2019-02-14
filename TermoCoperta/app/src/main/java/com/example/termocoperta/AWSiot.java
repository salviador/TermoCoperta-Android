package com.example.termocoperta;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.example.termocoperta.ClassDati.Constanti;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.example.termocoperta.ClassDati.Constanti.REGION;

public class AWSiot {
    private static final String CUSTOMER_SPECIFIC_IOT_ENDPOINT = Constanti.CUSTOMER_SPECIFIC_IOT_ENDPOINT;


    private  final String TAG = "AWS";
    private AWSIotClient mIotAndroidClient;
    private AWSIotMqttManager mqttManager;
    private String clientId;
    private static Region region;
    private static CognitoCachingCredentialsProvider credentialsProvider;
    private static  AWSCredentialsProvider awsCredentialsProvider;
    private Context context;

    public Boolean IsConnect;


    public AWSiot(Context context){
        this.context = context;

        IsConnect=false;

        clientId = UUID.randomUUID().toString();

        // Inizializza il provider di credenziali Amazon Cognito
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                Constanti.IDENTITY_POOL_ID, // ID pool di identità
                Constanti.REGION
                // Regione
        );
        region = Region.getRegion(Constanti.REGION);


        awsCredentialsProvider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                BasicAWSCredentials awsCreds = new BasicAWSCredentials( Constanti.ACCESS_KEY, Constanti.SECRET_KEY);
                return awsCreds;
            }

            @Override
            public void refresh() {

            }
        };

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_IOT_ENDPOINT);
        mqttManager.setKeepAlive(10);

        mIotAndroidClient = new AWSIotClient(awsCredentialsProvider);
        mIotAndroidClient.setRegion(region);

/*
        final CountDownLatch latch = new CountDownLatch(1);
        AWSMobileClient.getInstance().initialize(
                context,
                new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails result) {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        latch.countDown();
                        Log.e(TAG, "onError: ", e);
                    }
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/


    }


    private static onConnect _onConnect;

    public interface onConnect{
        void IsConnect(Boolean stato);
    }

    public void register_callback_connessione(final onConnect _ononConnect){
        this._onConnect = _ononConnect;
    }


    public void Connetti(){
        try {
            mqttManager.connect(awsCredentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {

                    //Log.d(TAG, "Status = " + String.valueOf(status));

                    if(status == AWSIotMqttClientStatus.Connected) {
                        IsConnect=true;
                        if(_onConnect != null){
                            _onConnect.IsConnect(true);
                        }
                    }else
                    {
                        IsConnect=false;
                        if(_onConnect != null){
                            _onConnect.IsConnect(false);
                        }
                    }
                }
            });
        } catch (final Exception e) {
            Log.e(TAG, "Connection error.", e);
            IsConnect=false;
        }
    }

    public void publish(String topic, String msg) {
        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(TAG, "Publish error.", e);
        }
    }

    public void disconnect() {
        try {
            mqttManager.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Disconnect error.", e);
            IsConnect=false;
        }
    }


}
