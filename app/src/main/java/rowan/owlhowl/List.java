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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ryanm on 11/3/2017.
 */

public class List extends AppCompatActivity {
    Button updateHowls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listofmessages);

        String[] howls = {"Yo what's up?", "This place is awesome.", "Nothing but positive vibes!", "Don't eat the fish."};
        ListAdapter howlsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, howls);
        ListView howlsListView = (ListView) findViewById(R.id.howlViews);
        howlsListView.setAdapter(howlsAdapter);

        howlsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String message = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(List.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

}

