package rowan.owlhowl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by ryanm on 10/8/2017.
 */

public class Pop extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);


        String[] message = {"This place is really nice.", "OMG can you believe it?", "Dude! Do not go in the bathroom", "You need to try the fish.",
                            "These aren't the droids you're looking for. Do or do not there is no try. Luke use the force. You are not my father." +
                                    "The power of the dark side of the force.", "Get out of town."};
        ListAdapter myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, message);
        ListView messageListView = (ListView) findViewById(R.id.messageListView);
        messageListView.setAdapter(myAdapter);
    }
}
