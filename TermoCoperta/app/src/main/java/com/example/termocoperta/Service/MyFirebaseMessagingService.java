package com.example.termocoperta.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.termocoperta.AltervistaMyServer;
import com.example.termocoperta.ClassDati.StatoCoperta;
import com.example.termocoperta.ClassDati.Constanti;
import com.example.termocoperta.MainActivity;
import com.example.termocoperta.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;


//https://www.itsalif.info/content/android-volley-tutorial-http-get-post-put


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "FIREBASE";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        final String newr=s;
        Log.d(TAG,"TOKEN_FIREBASE: " + s);
/*
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, newr, duration);
                toast.show();
            }
        });
*/
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //preferences.edit().putString("Token",s).apply();

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //final String token = preferences.getString("Token","");

        UpdateToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("MESSAGE","message");

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getData().isEmpty()) {
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),true);
            }
        }
        if (remoteMessage.getData().size() > 0) {
            if((remoteMessage.getData().containsKey("date"))&&(remoteMessage.getData().containsKey("time"))&&
                    (remoteMessage.getData().containsKey("State"))) {
                StatoCoperta statoCoperta = new StatoCoperta();
                statoCoperta.date = remoteMessage.getData().get("date");
                statoCoperta.time = remoteMessage.getData().get("time");
                statoCoperta.State = remoteMessage.getData().get("State");
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    statoCoperta.datetime = ft.parse(remoteMessage.getData().get("date").toString() + " " + remoteMessage.getData().get("time").toString());
                }catch (ParseException pe){}

                if(statoCoperta.State.toUpperCase().equals("ON")) {
                    showNotification("Termo Coperta", statoCoperta.State.toUpperCase(), true);
                }else{
                    showNotification("Termo Coperta", statoCoperta.State.toUpperCase(), false);
                }
                Intent intent = new Intent(Constanti.BROADCAST_ACTION);
                intent.putExtra(Constanti.BROADCAST_FIREBASE_MESSAGE,statoCoperta.toString());
                sendBroadcast(intent);
            }
        }
    }

    private void showNotification(String title, String body, Boolean action){
        //Aggiungere solo se si tocca il banner notifica apre app
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.termocoperta";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

       // Uri urisound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //final Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        //final Uri sound = Uri.parse("android.resource://com.example.termocoperta/" + R.raw.notification);
        final Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notification);

        String sa =getPackageName();
        String sa4 =getPackageResourcePath();


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                //.setVibrate(new long[]{0,1000,500,1000})
                .setVibrate(new long[]{0,500,500})
                //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSound(sound)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationBuilder.setContentIntent(resultPendingIntent);  //Aggiungere solo se si tocca il banner notifica apre app

        if(action){
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.switch_on_icon96));
            notificationBuilder.setSmallIcon(R.drawable.switch_on_icon96);

        }else{
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.switch_off_icon96));
            notificationBuilder.setSmallIcon(R.drawable.switch_off_icon96);
        }

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());

    }


    private void UpdateToken(String token){

        final String android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        AltervistaMyServer a = new AltervistaMyServer(this);
        a.Set_DataDB(android_id,token);
    }
}
