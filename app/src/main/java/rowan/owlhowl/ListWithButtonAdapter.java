package rowan.owlhowl;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Leif on 11/17/2017.
 */

public class ListWithButtonAdapter extends ArrayAdapter<String> {
    private int layout;
    private String identifier;
    //ViewHolder mainViewholder = null;
    public ListWithButtonAdapter(Context context, int resource, String[] howls, String identifier) {
        super(context, resource, howls);
        layout = resource;
        this.identifier=identifier;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewholder = null;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            //viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
            viewHolder.buttonUp = (Button) convertView.findViewById(R.id.list_item_up_btn);
            viewHolder.buttonDwn = (Button) convertView.findViewById(R.id.list_item_dwn_btn);

            convertView.setTag(viewHolder);
        }
        mainViewholder = (ViewHolder) convertView.getTag();
        mainViewholder.title.setText(getItem(position));
        mainViewholder.buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Button was clicked" + getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
        mainViewholder.buttonDwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Button was clicked" + getItem(position), Toast.LENGTH_SHORT).show();
            }

        });

        return convertView;
    }

    public class ViewHolder {
        //ImageView thumbnail;
        TextView title;
        Button buttonUp;
        Button buttonDwn;
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... arg0){
            HttpURLConnection myConnection = null;
            try{
                //TODO change to real URL
                URL owlHowlPostEndpoint = new URL("http://ec2-34-230-76-33.compute-1.amazonaws.com:8080/message");
                //build request data
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("message", arg0[0]);
                params.put("vote", arg0[1]);
                params.put("identifier", arg0[2]);
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
                    return readInput(myConnection.getInputStream());
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

            Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
        }
    }

    private String readInput(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        while((line = in.readLine()) != null){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
}