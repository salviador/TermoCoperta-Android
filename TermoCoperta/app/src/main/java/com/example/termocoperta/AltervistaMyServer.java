package com.example.termocoperta;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.termocoperta.ClassDati.Constanti;
import com.example.termocoperta.ClassDati.StatoCoperta;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AltervistaMyServer {

    public interface VolleyCallBack {
        void onSuccess(String token, Boolean error);
    }
    public interface StateCopertaCallback {
        void onSuccess(StatoCoperta stato, Boolean error);
    }

    private Context context;

    public AltervistaMyServer(Context contex){
        this.context = contex;
    }

    public void Get_TokenDB(String serial, final AltervistaMyServer.VolleyCallBack callBack){

        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constanti.GET_DB_TOKEN_url + serial,
                null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("TOKEN");
                            //String garment_color=response.getString("SERIAL");

                            callBack.onSuccess(token,false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callBack.onSuccess("",true);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Autorization",Constanti.url_AUTHORIZATON);
                return headers;
            }
        };
        queue.add(myRequest);
    }

    public void Set_DataDB(String serial, String token)
    {
        RequestQueue queue = Volley.newRequestQueue(context);  // this = context

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("SERIAL", serial);// Build.getSerial());
        jsonParams.put("TOKEN", token);

        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constanti.SET_DB_TOKEN_url,
                new JSONObject(jsonParams),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Autorization",Constanti.url_AUTHORIZATON);
                return headers;
            }
        };
        try {
            queue.add(myRequest).wait();
        }catch (Exception e){}
    }


    public void Get_Stato_Coperta( final AltervistaMyServer.StateCopertaCallback callBack){

        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constanti.GET_DB_STATO_url,
                null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            StatoCoperta s = new StatoCoperta();
                            s.date = response.getString("date");
                            s.time = response.getString("time");
                            s.State = response.getString("State");
                            SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            try {
                                s.datetime = ft.parse(s.date + " " +s.time);
                            }catch (ParseException pe){}

                            callBack.onSuccess(s,false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StatoCoperta s = null;
                        callBack.onSuccess(s,true);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Autorization",Constanti.url_AUTHORIZATON);
                return headers;
            }
        };
        queue.add(myRequest);
    }





}
