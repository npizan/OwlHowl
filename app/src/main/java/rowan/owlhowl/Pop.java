package rowan.owlhowl;

import android.app.ListActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by ryanm and Leifw on 10/8/2017.
 */

public class Pop extends AppCompatActivity {
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        expandableListView = (ExpandableListView) findViewById(R.id.listView);
        List<String> Headings = new ArrayList<String>();
        List<String> L1 = new ArrayList<String>();
        List<String> L2 = new ArrayList<String>();
        List<String> L3 = new ArrayList<String>();
        HashMap<String, List<String>> ChildList = new HashMap<String, List<String>>();
        String heading_items[] = getResources().getStringArray(R.array.header_titles);
        String l1[] = getResources().getStringArray(R.array.h1_items);
        String l2[] = getResources().getStringArray(R.array.h2_items);
        String l3[] = getResources().getStringArray(R.array.h3_items);
        for (String title : heading_items)
        {
            Headings.add(title);
        }
        for (String title : l1)
        {
            L1.add(title);
        }
        for (String title : l2)
        {
            L2.add(title);
        }
        for (String title : l3)
        {
            L3.add(title);
        }
        ChildList.put(Headings.get(0),L1);
        ChildList.put(Headings.get(1),L2);
        ChildList.put(Headings.get(2),L3);
        PopAdapter myAdapter = new PopAdapter(this, Headings, ChildList);
        expandableListView.setAdapter(myAdapter);
    }

}
