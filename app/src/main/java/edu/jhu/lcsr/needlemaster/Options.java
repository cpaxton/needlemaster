package edu.jhu.lcsr.needlemaster;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import edu.jhu.lcsr.grid.needlegame.GameDataStore;

import static android.os.Environment.MEDIA_MOUNTED;


public class Options extends Activity {

    File file;
    File subDirectory;
    FileOutputStream fo;
    OutputStreamWriter fow;

    String dirName;

    GameDataStore gds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        gds = new GameDataStore(getApplicationContext());

        TextView filesView = (TextView) findViewById(R.id.filesView);
        Button clearButton = (Button) findViewById(R.id.deleteButton);
        CheckBox collect = (CheckBox) findViewById(R.id.storeDataCheckBox);
        CheckBox randomize = (CheckBox) findViewById(R.id.randomStartBox);
        Button backButton = (Button) findViewById(R.id.optionsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        collect.setChecked(gds.checkSaveData());
        collect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gds.setCollectData(isChecked);
            }
        });

        randomize.setChecked(gds.checkRandomStart());
        randomize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gds.setRandomStart(isChecked);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dirName = extras.getString("DATA_DIRECTORY","needle_master_trials");
        } else {
            dirName = "needle_master_trials";
        }

        String storageState = Environment.getExternalStorageState();
        if (MEDIA_MOUNTED.equals(storageState)) {
            subDirectory = new File(Environment.getExternalStorageDirectory(), dirName);
            //if (!subDirectory.mkdir()) {
            //    System.err.println("Directory for trials not created!");
            //}
            String[] files = subDirectory.list();
            if (files.length > 0) {
                filesView.setText(String.format(getString(R.string.files_button_text),files.length));
            } else {
                filesView.setText(getString(R.string.files_empty_text));
                clearButton.setEnabled(false);
            }
        } else {
            filesView.setText(getString(R.string.files_empty_text));
            clearButton.setEnabled(false);
        }

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subDirectory != null) {
                    String[] files = subDirectory.list();
                    for (String str: files) {
                        File tmp = new File(subDirectory, str);
                        tmp.delete();
                    }
                    finish();
                }
            }
        });
    }


}
