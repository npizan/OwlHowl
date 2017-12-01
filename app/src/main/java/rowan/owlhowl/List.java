package rowan.owlhowl;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarInputStream;

import rowan.owlhowl.MapsActivityOwlHowl;

/**
 * Created by Ryan, Brandon, Will, Leif, Cullen on 11/3/2017.
 * Class List creates the ListView that the main messages
 * are displayed on.
 */

public class List extends AppCompatActivity {
    Button updateHowls;
    String data = "";
    Button temp;
    MapsActivityOwlHowl ma = new MapsActivityOwlHowl();


    /**
     * OnCreate is the constructor of this activity.  When the
     * "GetHowls" button is pressed on the main activity, it sends
     * a get request to the database and starts an Instance of
     * Class List.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listofmessages);
        data = getIntent().getStringExtra("howls");
        displayMes(data);

        // The update howls button on click listener
        /*updateHowls = (Button) findViewById(R.id.btupdateMessages);
        updateHowls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data = getIntent().getStringExtra("howls");
                displayMes(data);
                Toast.makeText(List.this, "Updated HOWLS", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /**
     * displayMes() is responsible for grabbing the data
     * from the getRequest in class MapsActivity and display that
     * data to the ListView
     * @param data
     */
    public void displayMes(String data){
        try{
            JSONArray json = new JSONArray(data);
            String[] howls = new String[json.length()];
            for(int i=0; i<json.length();i++){
                howls[i]= json.getJSONObject(i).getString("message");
            }
            ListAdapter howlsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, howls);
            ListView howlsListView = (ListView) findViewById(R.id.howlViews);
            howlsListView.setAdapter(new ListWithButtonAdapter(this, R.layout.list_with_buttons,howls));
            //howlsListView.setAdapter(howlsAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}