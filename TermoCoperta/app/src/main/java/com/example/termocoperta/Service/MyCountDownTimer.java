package com.example.termocoperta.Service;

import android.os.CountDownTimer;
import com.example.termocoperta.ClassDati.StatoCoperta;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MyCountDownTimer extends CountDownTimer {
    private Boolean _CancelTimer = false;
    public void CancelTimer(Boolean cancelTimer){
        this._CancelTimer = cancelTimer;
    }
    public boolean isRunning = false;


    private StatoCoperta statoCoperta;

    private static Callback_update callbackUpdate;

    public interface Callback_update{
        void updateTimer(String time);
        void updateTimerFinish(String time);
    }


    public MyCountDownTimer(long millisInFuture, long countDownInterval, Callback_update cupd){
        super(millisInFuture,countDownInterval);

        this.callbackUpdate = cupd;
        this.isRunning = false;
    }


    public void UpdateStatoCoperta(StatoCoperta statoCoperta){
        this.statoCoperta=statoCoperta;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.isRunning = true;

        Date startTime = new Date();

        long diff = Math.abs(startTime.getTime() - statoCoperta.datetime.getTime());
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);

        DecimalFormat df2 = new DecimalFormat("00");

        if(_CancelTimer){
            //this.cancel();
            super.cancel();
            this.isRunning = false;
        }
        if(callbackUpdate != null){
            callbackUpdate.updateTimer(Long.toString(diffHours) + ":" + df2.format(diffMinutes) + ":" + df2.format(diffSeconds) );
        }
    }

    @Override
    public void onFinish() {
        this.isRunning = false;

        if(callbackUpdate != null){
            callbackUpdate.updateTimerFinish("0:00:00");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.cancel();
        super.finalize();
    }
}
