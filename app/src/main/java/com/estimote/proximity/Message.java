package com.estimote.proximity;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Message {
    private String beaconId = "testBeacon";
    private double proximity = 2;
    private long timestamp = 1648428342;
    private String device_id = UUID.randomUUID().toString();
    private String message;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Message(String id, double distance, String message){
        beaconId = id;
        proximity = distance;
        timestamp = System.currentTimeMillis() / 1000L;
        this.message = message;
    }



    public String toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("iniat",device_id);
        json.put("resp",beaconId);
        json.put("distance",proximity);
        json.put("ts",timestamp);
        return json.toString();
    }

    public String getMessage(){
        return beaconId+"\n" + message + "\n" +timestamp + "\n"+device_id;
    }
}
