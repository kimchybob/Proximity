package com.estimote.proximity;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

public class Endpoint {
    private static String path = "https://sa3ykxqpng.execute-api.us-east-2.amazonaws.com/testpostActivity";

    private static ArrayList<String> msgList = new ArrayList<>();

    public void ready(){
        final long timeInterval = 60000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        System.out.println("sleep");
                        Thread.sleep(timeInterval);
                        System.out.println("over");
                        post();
                        msgList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void collect(String msg) {
        msgList.add(msg);
        System.out.println("current msgList:" + msgList.toString());
    }

    public void post() {
        try {
            HttpClient client= new DefaultHttpClient();
            HttpPost request = new HttpPost(path);
            request.setHeader("Content-type", "application/json");
            String output = "{\"assets:" + msgList.toString() + "}";
            request.setEntity(new StringEntity(output));
            System.out.println("body:" + output);
            HttpResponse resp = client.execute(request);
            HttpEntity entity = resp.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
