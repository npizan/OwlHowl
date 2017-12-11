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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
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
    // Button updateHowls;
    JSONArray data = null;
    JSONArray changes = new JSONArray();
    //TODO probably wont use; need for unique votes if we get that far
    String identifier = "";


    /**
     * OnCreate is the constructor of this activity.  When the
     * "GetHowls" button is pressed on the main activity, it sends
     * a get request to the database and starts an Instance of
     * Class List.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listofmessages);
        identifier = getIntent().getStringExtra("identifier");
        try {
            data = new JSONArray(getIntent().getStringExtra("howls"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
     *
     * @param json
     */
    public void displayMes(final JSONArray json) {

        final String[] howls = new String[json.length()];
        for (int i = 0; i < json.length(); i++) {
            try {
                howls[i] = json.getJSONObject(i).getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ListView howlsListView = (ListView) findViewById(R.id.howlViews);
        howlsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();
                try {
                    if (viewId == R.id.list_item_up_btn) {
                        upVote(data.getJSONObject(position).getInt("messageID"), position);
                    } else if (viewId == R.id.list_item_dwn_btn) {
                        downVote(data.getJSONObject(position).getInt("messageID"), position);
                    }
                    // this block is for when the text in the view is clicked
                    // useful for opening a comment view, displaying full message etc.
//                    else {
//                        Toast.makeText(getApplicationContext(), "Text clicked " + howls[position], Toast.LENGTH_SHORT).show();
//                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        howlsListView.setAdapter(new ListWithButtonAdapter(this, R.layout.list_with_buttons, howls, data));
        //howlsListView.setAdapter(howlsAdapter);
    }

    private void upVote(int messageID, int position) throws JSONException {
        //if the position is null, no change has been made; create a change
        if(changes.isNull(position)) {
            HashMap<String, Object> change = new HashMap<>();
            change.put("messageID", messageID);
            change.put("vote", 1);
            changes.put(position, new JSONObject(change));
        }else //if the change already exists and it's an upvote, remove the change
            if(changes.getJSONObject(position).getInt("vote")==1){
            changes.put(position,null);
        }else //if the change already exists and is a downvote, change it to an upvote
            if(changes.getJSONObject(position).getInt("vote")==-1){
                changes.getJSONObject(position).put("vote",1);
            }
        //TODO adjust local vote display
    }

    private void downVote(int messageID, int position) throws JSONException {
        //if the position is null, no change has been made; create a change
        if(changes.isNull(position)) {
            HashMap<String, Object> change = new HashMap<>();
            change.put("messageID", messageID);
            change.put("vote", -1);
            changes.put(position, new JSONObject(change));
        }else //if the change already exists and it's an downvote, remove the change
            if(changes.getJSONObject(position).getInt("vote")==-1){
                changes.put(position,null);
            }else //if the change already exists and is a upvote, change it to a downvote
                if(changes.getJSONObject(position).getInt("vote")==1){
                    changes.getJSONObject(position).put("vote",-1);
                }
        //TODO adjust local vote display
    }

    @Override
    public void onBackPressed() {
        if(changes.length()!=0) {
            JSONArray postChanges = new JSONArray();
            for (int i = 0; i < changes.length(); i++) {
                if (!changes.isNull(i)) {
                    try {
                        postChanges.put(changes.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            new SendPostRequest().execute(postChanges);
        }
        finish();
    }

    public class SendPostRequest extends AsyncTask<JSONArray, Void, String> {
        protected String doInBackground(JSONArray... arg0) {
            HttpURLConnection myConnection = null;
            try {
                //TODO change to real URL
                URL owlHowlPostEndpoint = new URL("http://ec2-34-230-76-33.compute-1.amazonaws.com:8080/rating");
                //build request data
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("ratingChanges", arg0[0]);
//                params.put("vote", arg0[1]);
//                params.put("identifier", arg0[2]);
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) {
                        postData.append('&');
                    }
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

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

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return readInput(myConnection.getInputStream());
                } else {
                    return "HTTP Error : " + responseCode;
                }
            } catch (Exception e) {
                return "Caught exception: " + e.getMessage();
            } finally {
                myConnection.disconnect();
            }
        }
    }

    private String readInput(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
}