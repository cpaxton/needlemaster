package edu.jhu.lcsr.grid.needlegame;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Accesses game information stored on the device.
 * Created by cpaxton on 1/13/15.
 */
public class GameDataStore {

    File saveData;



    public GameDataStore(Context context) {

        saveData = new File(context.getFilesDir(), ".saveData");

        checkSaveData();
    }

    public boolean checkSaveData() {
        return saveData.exists();
    }

    public void setCollectData(boolean isChecked) {
        if (isChecked && !saveData.exists()) {
            try{
                saveData.createNewFile();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else if (saveData.exists()) {
            saveData.delete();
        }
    }
}
