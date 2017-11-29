package rowan.owlhowl;

import android.content.Context;
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

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Leif on 11/17/2017.
 */

public class ListWithButtonAdapter extends ArrayAdapter<String> {
    private int layout;
    //ViewHolder mainViewholder = null;
    public ListWithButtonAdapter(Context context, int resource, String[] howls) {
        super(context, resource, howls);
        layout = resource;
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
            /*viewHolder.buttonUp = (Button) convertView.findViewById(R.id.list_item_up_btn);
            viewHolder.buttonDwn = (Button) convertView.findViewById(R.id.list_item_dwn_btn);
            viewHolder.buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button was clicked" + position, Toast.LENGTH_SHORT).show();
                }

            });
            viewHolder.buttonDwn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button was clicked" + position, Toast.LENGTH_SHORT).show();
                }

            });*/
            convertView.setTag(viewHolder);
            //return convertView;
        }



        mainViewholder = (ViewHolder) convertView.getTag();

        mainViewholder.title.setText(getItem(position));
        //return convertView;


        return convertView;

    }

    public class ViewHolder {

        //ImageView thumbnail;
        TextView title;
        //Button buttonUp;
        //Button buttonDwn;
    }
}
