package com.example.termocoperta.ClassDati;

import com.amazonaws.regions.Regions;

public final class Constanti {
    public static String BROADCAST_ACTION = "com.example.termocoperta";
    public static String BROADCAST_FIREBASE_TOKEN = "com.example.termocoperta.FireBase.TOKEN";
    public static String BROADCAST_FIREBASE_MESSAGE = "com.example.termocoperta.FireBase.MESSAGE";


    //---SIMPLE DATABASE php---
    public static String GET_DB_TOKEN_url = "";
    public static String SET_DB_TOKEN_url = "";
    public static String GET_DB_STATO_url = "";
    public static String url_AUTHORIZATON = "";



    //---AWS AMAZON---
    //imap
    public static String ACCESS_KEY = "xxxxxxxxxxxxxxx";
    public static String SECRET_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static Regions REGION = Regions.EU_WEST_1;

    //iot impostazioni
    public static String CUSTOMER_SPECIFIC_IOT_ENDPOINT = "xxxxxxxxxxxx-ats.iot.eu-west-1.amazonaws.com";

    //Cognito
    public static String IDENTITY_POOL_ID = "eu-west-1:zzzzzzzz-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
}

