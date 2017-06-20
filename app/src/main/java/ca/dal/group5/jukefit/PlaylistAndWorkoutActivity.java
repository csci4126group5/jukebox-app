package ca.dal.group5.jukefit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ca.dal.group5.jukefit.API.APISpec;
import ca.dal.group5.jukefit.API.MockAPI;
import ca.dal.group5.jukefit.API.RequestHandler;
import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Preferences.PreferencesService;

import static ca.dal.group5.jukefit.R.id.determinateBar;


public class PlaylistAndWorkoutActivity extends AppCompatActivity implements SensorEventListener {

//    public String fullPath;
//    File file;
//    private File[] listFile;
//    private String[] FilePathStrings;
//    private String[] FileNameStrings;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;

    ListView leaderboardListView;
    TextView stepsDifferenceTextView;
    TextView stepsTakenTextView;
    ProgressBar stepsProgress;

    APISpec ServerAPI;
    PreferencesService prefs;

    String groupCode = "<na>";
    String groupName = "<na>";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_and_workout);
        //setContentView(R.grouplistitem.activity_competition_view);

        groupCode = getIntent().getStringExtra("GROUP_CODE");
        groupName = getIntent().getStringExtra("GROUP_NAME");

        ServerAPI = new MockAPI();
        prefs = new PreferencesService(this);

        leaderboardListView = (ListView)findViewById(R.id.playerProgress);
        leaderboardListView.setItemsCanFocus(false);
        leaderboardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        stepsDifferenceTextView = (TextView) findViewById(R.id.remainingSteps);

        stepsTakenTextView = (TextView) findViewById(R.id.stepCount);

        stepsProgress = (ProgressBar) findViewById(determinateBar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabGroup);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PlaylistAndWorkoutActivity.this, CompetitionView.class);
//                //finish();
//                startActivity(intent);
//            }
//        });
//        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
//        fabAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PlaylistAndWorkoutActivity.this, AddSongsActivity.class);
//                finish();
//                startActivity(intent);
//            }
//        });
//        final FloatingActionButton fabStart = (FloatingActionButton) findViewById(R.id.fabStart);
//        fabStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        ActivityCompat.requestPermissions(PlaylistAndWorkoutActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        ActivityCompat.requestPermissions(PlaylistAndWorkoutActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//
//        if (!Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JukeFit/";
//            boolean success = true;
//            try
//            {
//                file = new File(fullPath);
//                if (!file.exists())
//                    success = file.mkdirs();
//                System.out.println("Full Path: "+fullPath+" :+ "+file.getAbsolutePath());
//                if (success) {
//                    System.out.println("Full Path: "+fullPath);
//                    System.out.println("Success:"+file.exists()+fullPath);
//                } else {
//                    System.out.println("Failure:"+file.exists());
//                }
//            }
//            catch (Exception e) {
//                System.out.println( "Error: "+e.getMessage());
//            }
//            if(file.listFiles() != null)
//            {
//                listFile = file.listFiles();
//                int len = listFile.length;
//                System.out.println("Length:" + len);
//            }
//        }
//
//        if (file.isDirectory() && file.listFiles() != null) {
//            listFile = file.listFiles();
//            FilePathStrings = new String[listFile.length];
//            FileNameStrings = new String[listFile.length];
//
//            for (int i = 0; i < listFile.length; i++) {
//                System.out.println("List File Size: "+listFile[i].length());
//                if(listFile[i].length() != 0) {
//                    FilePathStrings[i] = listFile[i].getAbsolutePath();
//                    FileNameStrings[i] = listFile[i].getName();
//                    System.out.println("FilePathStrings: " + FilePathStrings[i]);
//                    System.out.println("FileNameStrings: " + FileNameStrings[i]);
//                    System.out.println("List File Size 2: "+listFile[i].length());
//                }
//            }
//        }
//
//        leaderboardListView = (ListView)findViewById(R.id.SongListView);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.grouplistitem.group_song_list, R.id.SongName, FileNameStrings);
//        leaderboardListView.setAdapter(adapter);
//        leaderboardListView.setItemsCanFocus(false);
//        leaderboardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }

        this.setTitle(groupName);
        updateInformation(0);
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
        Log.d("sensor", event.toString());
        stepsTakenTextView.setText(String.valueOf(event.values[0]).substring(0,String.valueOf(event.values[0]).length() - 2));
        final String steps = stepsTakenTextView.getText().toString();
        ServerAPI.updateScore(null, prefs.getDeviceID(), Integer.parseInt(steps), new RequestHandler<Member>() {
            @Override
            public void callback(Member result) {
                updateInformation(Integer.parseInt(steps));
            }
        });
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

    void updateInformation(final int currentSteps) {
        ServerAPI.groupInformation(groupCode, new RequestHandler<Group>() {
            @Override
            public void callback(Group result) {
                setLeaderboard(result);
                setStepsProgress(currentSteps);
                setStepsDifference(result, currentSteps);
            }
        });
    }

    void setLeaderboard(Group group) {
        ArrayList<String> memberInformation = new ArrayList<String>();
        for (Member member : group.getSortedMembers()) {
            memberInformation.add(member.getName() + "                                       " + member.getScore());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.playerlistitem, R.id.playerName, memberInformation);
        leaderboardListView.setAdapter(adapter);
    }

    void setStepsProgress(int currentSteps) {
        stepsProgress.setProgress(currentSteps / 100);
    }

    void setStepsDifference(Group group, int currentSteps) {
        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (Member member : group.getSortedMembers()) {
            scores.add(member.getScore());
        }

        if(currentSteps >= (scores.get(0))) {
            int diff = currentSteps - scores.get(1);
            stepsDifferenceTextView.setText("You lead by "+ diff +" steps");
            stepsDifferenceTextView.setTextColor(Color.GREEN);
        } else {
            int diff = scores.get(0) -  currentSteps;
            stepsDifferenceTextView.setText("You trail by "+ diff + " steps");
            stepsDifferenceTextView.setTextColor(Color.RED);
        }
    }
}
