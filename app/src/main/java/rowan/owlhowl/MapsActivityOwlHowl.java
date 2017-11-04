package rowan.owlhowl;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main class. Sets up and interacts with the map.
 *
 * @author Ryan Godfrey, Adam, Leif, Will, Brandon, Cullen
 * @version 1.awesome
 */

public class MapsActivityOwlHowl extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // creates the map
    private final static int MY_PERMISSIONS_FINE_LOCATION = 101;
    ZoomControls zoom;
    Button markBt;
    Button clear;
    Button post;
    Button getMessages;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    LatLng myLocation;
    Circle circle;
    List<Marker> mMarkers = new ArrayList<Marker>();
    //Temp
    ExpandableListView expandableListView;


    // Sets the map up.  This is called first.  When the
    // screen is tilted it will start here by setting up
    // the map again.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_owl_howl);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //
        getMessages = (Button) findViewById(R.id.btGetMes);
        getMessages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SendGetRequest().execute();
                //opens view of messages
                startActivity(new Intent(MapsActivityOwlHowl.this, rowan.owlhowl.List.class));

            }
        });

        //Creates the zoom in and out environment
        zoom = (ZoomControls) findViewById(R.id.zcZoom);
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        // Mark button onClickListener

        markBt = (Button) findViewById(R.id.btMark);
        markBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng myLocation = getLocation();
                //double latti = myLocation.latitude;
                //double longi = myLocation.longitude;
                //String lat = String.valueOf(latti);
                //String lon = String.valueOf(longi);

                MarkerOptions options = new MarkerOptions()
                        .position(myLocation)
                        .title("POST an anonymous HOWL at the top,")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.profhead))
                        .snippet("or press the Get HOWLS button below.");
                Marker marker = mMap.addMarker(options);
                marker.showInfoWindow();
                // add the maker with the following options
                mMarkers.add(marker);
            }
        });

        // Post button onClickListener
        post = (Button) findViewById(R.id.btPost);
        post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText postText = (EditText) findViewById(R.id.etLocationEntry);
                String pos = postText.getText().toString();
                new SendPostRequest().execute(pos);
                postText.setText("");
            }
        });

        // Clear button onClickListener
        clear = (Button) findViewById(R.id.btClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMarkers();

            }
        });

        // calls the locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //getLocation(); // not used
    }
    // ******************   End onCreate() Area *********************


    // Remove the markers from the arrayList
    // This method needed to be created so I could
    // keep the yellow location circle on the map
    // when the map.clear() was being called.
    // I added the markers to an arrayList and
    // wipe the list out when the clear button
    // is selected.
    private void removeMarkers() {
        for(Marker marker: mMarkers){
            marker.remove();
        }
        mMarkers.clear();
    }

    // This is a custom getLocation method.  I had
    // to create it so I could find current location
    // upon start up of the app.  This is called and
    // returns a LatLng object.  LatLng contains
    // two Double types as params
    private LatLng getLocation() {
        LatLng latlog = new LatLng(0, 0);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                LatLng ll = new LatLng(latti, longi);
                latlog = ll;
            }
        }
        return latlog;
    }




    // Draw a circle on the map
    private Circle drawCircle(LatLng latLng){
        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(4023) // in meters = 2.5 miles
                .fillColor(0x30ead61c) // 30 is the amount of transparency ead61c is the color yellow
                .strokeColor(Color.BLACK) // this is the outline
                .strokeWidth(3);
        return mMap.addCircle(options);
    }

    /**
     * Asynchronous POST request.
     * All network operations have to be done off the main thread.
     * This allows UI to function normally while this is done in background
     */
    public class SendPostRequest extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... arg0){
            HttpURLConnection myConnection = null;
            try{
                URL owlHowlPostEndpoint = new URL("http://ec2-34-230-76-33.compute-1.amazonaws.com:8080/message");
                //build request data
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("message", arg0[0]);
                params.put("lat", getLocation().latitude);
                params.put("lng", getLocation().longitude);
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) {
                        postData.append('&');
                    }
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes =postData.toString().getBytes("UTF-8");

                //Set connection
                myConnection = (HttpURLConnection) owlHowlPostEndpoint.openConnection();
                myConnection.setReadTimeout(10000);
                myConnection.setConnectTimeout(10000);
                myConnection.setRequestMethod("POST");
                myConnection.setDoOutput(true);
                myConnection.setDoInput(true);
                myConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                myConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                //Write the data
                myConnection.getOutputStream().write(postDataBytes);

                int responseCode = myConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream(), "UTF-8"));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else{
                    return "HTTP Error : "+responseCode;
                }
            }catch(Exception e){
                return "Caught exception: "+e.getMessage();
            }finally{
                myConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }


    public class SendGetRequest extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... arg0){
            HttpURLConnection myConnection = null;
            try{
                //build URL/Get data
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("lat", getLocation().latitude);
                params.put("lng", getLocation().longitude);
                StringBuilder URLend = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (URLend.length() != 0) {
                        URLend.append('&');
                    }
                    URLend.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    URLend.append('=');
                    URLend.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }

                URL owlHowlGetEndpoint = new URL("http://ec2-34-230-76-33.compute-1.amazonaws.com:8080/message?" + URLend.toString());

                //Set connection
                myConnection = (HttpURLConnection) owlHowlGetEndpoint.openConnection();
                myConnection.setReadTimeout(10000);
                myConnection.setConnectTimeout(10000);

                int responseCode = myConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) myConnection.getContent(), "UTF-8"));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }
                    in.close();

                    return sb.toString();

                }
                else{
                    return "HTTP Error : "+responseCode;
                }
            }catch(Exception e){
                return "Caught exception: "+e.getMessage();
            }finally{
                myConnection.disconnect();
            }
        }

        private void parseGetMessage(Array array) {

        }



        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }


    //*********** End of misc methods area ***********************************

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Get current location and focus upon start up
        LatLng myLocation = getLocation();
        // move the camera to that location and zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13.0f));
        // Draw the circle that surrounds that location
        circle = drawCircle(myLocation);
        MarkerOptions options = new MarkerOptions()
                .position(myLocation)
                .title("POST an anonymous HOWL at the top,")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.profhead))
                .snippet("or press the Get HOWLS button below.");
        Marker marker = mMap.addMarker(options);
        marker.showInfoWindow();
        // add the maker with the following options
        mMarkers.add(marker);


        // On Map Click Listener.  It handles setting a marker
        // everywhere else on the map other than myLocation
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkers.add(mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Map Location")));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        // If the user has granted permission, make the current location button appear
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            // If not request for permission
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

}
