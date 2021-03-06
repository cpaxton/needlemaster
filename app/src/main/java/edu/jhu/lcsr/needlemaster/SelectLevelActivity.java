package edu.jhu.lcsr.needlemaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;


public class SelectLevelActivity extends Activity {

    ListView listView;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        listView = (ListView) findViewById(R.id.listView);
        String[] levels = {
                "Intro 1: Movement",
                "Intro 2a: Gates",
                "Intro 2b: Gates",
                "Intro 3a: Pairs of Gates",
                "Intro 3b: Chains of Gates",
                "Intro 4: Order",
                "Intro 5: Tissue",
                "Intro 6: Deep Tissue",
                "Extra 1: Start in Tissue",
                "Extra 2: Link Tissue",
                "Extra 3: Paired Gates",
                "Level 1: Getting Started",
                "Level 2: Carefully Now!",
                "Level 3: Big Dipper",
                "Level 4: The Mountain",
                "Level 5: The Loop",
                "Level 6: In and Out",
                "Level 7: Mind The Gap",
                "Level 8: Obstacle Avoidance",
                "Level 9: Pathfinder",
                "Level 10: The Maze"
        };
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < levels.length; i++) {
            list.add(levels[i]);
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // create listener to see what we selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), Game.class);
                i.putExtra("EXTRA_LEVEL_NUMBER", position);
                startActivity(i);
            }
        });
    }

}
