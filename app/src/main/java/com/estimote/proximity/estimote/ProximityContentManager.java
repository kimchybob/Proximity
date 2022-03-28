package com.estimote.proximity.estimote;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.estimote.proximity.Endpoint;
import com.estimote.proximity.Message;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContentManager {

    private Context context;
    private ProximityContentAdapter proximityContentAdapter;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;
    private ArrayList<String> beacons = new ArrayList<String>();
    private Endpoint ep = new Endpoint();

    public ProximityContentManager(Context context, ProximityContentAdapter proximityContentAdapter, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.proximityContentAdapter = proximityContentAdapter;
        this.cloudCredentials = cloudCredentials;
    }

    public void start() {

        ProximityObserver proximityObserver = new ProximityObserverBuilder(context, cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        double proximity = 2.0;

        ep.ready();
        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("test")
                .inCustomRange(proximity)
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> contexts) {

                        List<ProximityContent> nearbyContent = new ArrayList<>(contexts.size());
                        ArrayList<String> current = new ArrayList<String>();
                        for (ProximityZoneContext proximityContext : contexts) {
                            String title = proximityContext.getAttachments().get("test");
                            if (title == null) {
                                title = "unknown";
                            }
                            String subtitle = proximityContext.getDeviceId();
                            current.add(subtitle);
                            if(!beacons.contains(subtitle)){
                                Message msg = new Message(subtitle,proximity,"hello");
                                beacons.add(subtitle);
                                try {
                                    ep.collect(msg.toJson());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("app",msg.getMessage());
                            }
                            nearbyContent.add(new ProximityContent(title, subtitle));
                        }

                        for(String beacon : beacons){
                            if(!current.contains(beacon)){
                                Message msg = new Message(beacon,proximity,"good bye");
                                Log.d("app",msg.getMessage());
                            }
                        }

                        beacons = current;

                        proximityContentAdapter.setNearbyContent(nearbyContent);
                        proximityContentAdapter.notifyDataSetChanged();

                        return null;
                    }
                })
                .build();

        proximityObserverHandler = proximityObserver.startObserving(zone);
    }

    public void stop() {
        proximityObserverHandler.stop();
    }
}
