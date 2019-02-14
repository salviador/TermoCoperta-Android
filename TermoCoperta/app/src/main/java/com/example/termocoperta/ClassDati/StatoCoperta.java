package com.example.termocoperta.ClassDati;

import java.util.Date;

public class StatoCoperta {
    public String date;
    public String time;
    public String State;

    public Date datetime;

    @Override
    public String toString() {
        String s = "{\"date\":\""+ date + "\",\"time\":\"" + time + "\",\"State\":\"" + State + "\"}";
        return s;
    }
}
