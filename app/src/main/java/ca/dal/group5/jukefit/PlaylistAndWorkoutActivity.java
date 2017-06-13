package ca.dal.group5.jukefit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class PlaylistAndWorkoutActivity extends AppCompatActivity implements SensorEventListener {

    public String fullPath;
    File file;
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    ListView listview;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private TextView mStepsSinceReboot;
    public String Steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_and_workout);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistAndWorkoutActivity.this, AddSongsActivity.class);
                finish();
                startActivity(intent);
            }
        });
        final FloatingActionButton fabStart = (FloatingActionButton) findViewById(R.id.fabStart);
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ActivityCompat.requestPermissions(PlaylistAndWorkoutActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(PlaylistAndWorkoutActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                    .show();
        } else {
            fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JukeFit/";
            boolean success = true;
            try
            {
                file = new File(fullPath);
                if (!file.exists())
                    success = file.mkdirs();
                System.out.println("Full Path: "+fullPath+" :+ "+file.getAbsolutePath());
                if (success) {
                    System.out.println("Full Path: "+fullPath);
                    System.out.println("Success:"+file.exists()+fullPath);
                } else {
                    System.out.println("Failure:"+file.exists());
                }
            }
            catch (Exception e) {
                System.out.println( "Error: "+e.getMessage());
            }
            if(file.listFiles() != null)
            {
                listFile = file.listFiles();
                int len = listFile.length;
                System.out.println("Length:" + len);
            }
        }

        if (file.isDirectory() && file.listFiles() != null) {
            listFile = file.listFiles();
            FilePathStrings = new String[listFile.length];
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                System.out.println("List File Size: "+listFile[i].length());
                if(listFile[i].length() != 0) {
                    FilePathStrings[i] = listFile[i].getAbsolutePath();
                    FileNameStrings[i] = listFile[i].getName();
                    System.out.println("FilePathStrings: " + FilePathStrings[i]);
                    System.out.println("FileNameStrings: " + FileNameStrings[i]);
                    System.out.println("List File Size 2: "+listFile[i].length());
                }
            }
        }

        listview = (ListView)findViewById(R.id.SongListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group_song_list, R.id.SongName, FileNameStrings);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        
        mStepsSinceReboot = (TextView) findViewById(R.id.StepsCount);
        Steps = String.valueOf((mStepsSinceReboot));
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mStepsSinceReboot.setText(String.valueOf(event.values[0]).substring(0,String.valueOf(event.values[0]).length() - 2));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(AddSongsActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
