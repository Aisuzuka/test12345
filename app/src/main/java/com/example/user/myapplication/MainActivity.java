package com.example.user.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends FragmentActivity {
    private GoogleMap map;
    private TextView txtOutput;
    private String provider;
    private Switch aSwitch;
    private LocationManager locationMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*Lab 9 section 1*/
                    map.clear();
                    MarkerOptions m1 = new MarkerOptions();
                    m1.position(new LatLng(25.033611, 121.565000));
                    m1.title("Taipei 101");
                    m1.draggable(true);
                    map.addMarker(m1);


                    MarkerOptions m2 = new MarkerOptions();
                    m2.position(new LatLng(25.047924, 121.517081));
                    m2.title("Taipei Main Station");
                    m2.draggable(true);
                    map.addMarker(m2);


                    PolylineOptions poly = new PolylineOptions();
                    poly.add(new LatLng(25.033611, 121.565000));
                    poly.add(new LatLng(25.032728, 121.565137));
                    poly.add(new LatLng(25.033739, 121.527886));
                    poly.add(new LatLng(25.038716, 121.517758));
                    poly.add(new LatLng(25.045656, 121.519636));
                    poly.add(new LatLng(25.046200, 121.517533));

                    poly.color(Color.BLUE);
                    Polyline polyline = map.addPolyline(poly);
                    polyline.setWidth(10);
                    camerFocusOnMe((25.033611 + 25.047924) / 2, (121.517081 + 121.565000) / 2, 13);

                } else {
                    /*Lab 9 section 2*/
                    if (initLocationProvider()) {
                        map.clear();
                        whereAmI();
                    } else {
//            txtOutput.setText("Open GPS");
                        Toast.makeText(MainActivity.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean initLocationProvider() {
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            return true;
        }
        return false;
    }

    private void whereAmI() {
        GpsStatus.Listener gpsListener = (new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED:
                        Log.e("1", "GPS_EVENT_STARTED");
                        Toast.makeText(MainActivity.this, "GPS_EVEN_STARTED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.e("2", "GPS_EVENT_STOPPED");
                        Toast.makeText(MainActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.e("3", "GPS_EVENT_FIRST_FIX");
                        Toast.makeText(MainActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.e("4", "GPS_EVENT_SATELLITE_STATUS");
                        Toast.makeText(MainActivity.this, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        Location location = locationMgr.getLastKnownLocation(provider);
        locationMgr.addGpsStatusListener(gpsListener);
        camerFocusOnMe(location.getLatitude(), location.getLongitude(), 16);
        showMarkerMe(location.getLatitude(), location.getLongitude());
    }

    private void showMarkerMe(double lat, double lng) {
        MarkerOptions m1 = new MarkerOptions();
        m1.position(new LatLng(lat, lng));
        m1.title("I'm HERE!");
        m1.draggable(true);
        map.addMarker(m1);
    }

    private void camerFocusOnMe(double lat, double lng, float zoom) {
        CameraPosition cam = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(zoom).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cam));
    }
}
