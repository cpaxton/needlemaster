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
    File randomStart;

    public GameDataStore(Context context) {

        saveData = new File(context.getFilesDir(), ".saveData");
        randomStart = new File(context.getFilesDir(), ".randomStart");

        checkSaveData();
    }

    public boolean checkSaveData() {
        return saveData.exists();
    }

    public boolean checkRandomStart() { return randomStart.exists(); }

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

    public void setRandomStart(boolean isChecked) {
        if (isChecked && !saveData.exists()) {
            try{
                randomStart.createNewFile();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else if (saveData.exists()) {
            randomStart.delete();
        }
    }
}
