package com.example.termocoperta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.termocoperta.ClassDati.Constanti;

public class MyBroadcastNotificationReceiver extends BroadcastReceiver {

    //Implement for callBack main Activity
    interface IMyBroadcastNotificationReceiver{
        void sendMessage(String message);
    }

    private static IMyBroadcastNotificationReceiver iMyBroadcastNotificationReceiver;

    public void registerCallback(IMyBroadcastNotificationReceiver iMyBroadcastNotificationReceiver){
        this.iMyBroadcastNotificationReceiver = iMyBroadcastNotificationReceiver;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constanti.BROADCAST_ACTION)){
            Bundle rx = intent.getExtras();
            if(rx.containsKey(Constanti.BROADCAST_FIREBASE_MESSAGE)){
                String s = intent.getStringExtra(Constanti.BROADCAST_FIREBASE_MESSAGE);
                //CallBack Main activity
                if(iMyBroadcastNotificationReceiver!=null){
                    iMyBroadcastNotificationReceiver.sendMessage(s);
                }
            }
        }
    }

    /*
    //https://stackoverflow.com/questions/18923207/call-activity-method-from-broadcast-receiver
    //2° opzione x callback
    MainActivity m = null;
    void setMainActivityHandler(MainActivity main){
        this.main=main;
    }
    */

}
