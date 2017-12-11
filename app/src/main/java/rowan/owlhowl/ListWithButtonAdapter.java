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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

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
    private JSONArray data;
    //ViewHolder mainViewholder = null;
    public ListWithButtonAdapter(Context context, int resource, String[] howls, JSONArray data) {
        super(context, resource, howls);
        layout = resource;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            //viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
            viewHolder.title = convertView.findViewById(R.id.list_item_text);
            viewHolder.buttonUp = convertView.findViewById(R.id.list_item_up_btn);
            viewHolder.buttonDwn = convertView.findViewById(R.id.list_item_dwn_btn);
            viewHolder.handle = convertView.findViewById(R.id.handle);
            viewHolder.vote = convertView.findViewById(R.id.vote);

            convertView.setTag(viewHolder);
        }
        final ViewHolder mainViewholder = (ViewHolder) convertView.getTag();
        mainViewholder.title.setText(getItem(position));
        try {
            mainViewholder.handle.setText(data.getJSONObject(position).getString("handle"));
            mainViewholder.vote.setText(data.getJSONObject(position).getString("rating"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainViewholder.buttonUp.setOnClickListener(new View.OnClickListener() {
            boolean up=false;
            boolean down=false;

            @Override
            public void onClick(View v) {
                try {
                    String currentVote = data.getJSONObject(position).getString("rating");
                    int updatedVote;
                    ((ListView) parent).performItemClick(v, position, 0);
                    if (!up && !down) {
                        up = true;
                        updatedVote = Integer.valueOf(currentVote)+1;
                        currentVote=Integer.toString(updatedVote);
                        mainViewholder.vote.setText(currentVote);
                    } else if (up && !down) {
                        up = false;
                        mainViewholder.vote.setText(currentVote);
                    } else if (!up && down) {
                        up = true;
                        down = false;
                        updatedVote = Integer.valueOf(currentVote)+1;
                        currentVote=Integer.toString(updatedVote);
                        mainViewholder.vote.setText(currentVote);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mainViewholder.vote.invalidate();
            }
        });

        mainViewholder.buttonDwn.setOnClickListener(new View.OnClickListener() {
            boolean up=false;
            boolean down=false;

            @Override
            public void onClick(View v) {
                try {
                    String currentVote = data.getJSONObject(position).getString("rating");
                    int updatedVote;
                    ((ListView) parent).performItemClick(v, position, 0);
                    if (!up && !down) {
                        down = true;
                        updatedVote = Integer.valueOf(currentVote)-1;
                        currentVote=Integer.toString(updatedVote);
                        mainViewholder.vote.setText(currentVote);
                    } else if (!up && down) {
                        down = false;
                        mainViewholder.vote.setText(currentVote);
                    } else if (up && !down) {
                        up = false;
                        down = true;
                        updatedVote = Integer.valueOf(currentVote)-1;
                        currentVote=Integer.toString(updatedVote);
                        mainViewholder.vote.setText(currentVote);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mainViewholder.vote.invalidate();
            }
        });

        return convertView;
    }

    public class ViewHolder {
        //ImageView thumbnail;
        TextView title;
        TextView handle;
        TextView vote;
        Button buttonUp;
        Button buttonDwn;
    }
}