package ca.dal.group5.jukefit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ca.dal.group5.jukefit.API.APIService;
import ca.dal.group5.jukefit.API.APISpec;
import ca.dal.group5.jukefit.API.RequestHandler;
import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Model.Song;
import ca.dal.group5.jukefit.Preferences.PreferencesService;

import static ca.dal.group5.jukefit.R.id.determinateBar;

public class PlaylistAndWorkoutActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;

    ListView leaderboardListView;
    TextView stepsDifferenceTextView;
    TextView stepsTakenTextView;
    TextView speedTextView;
    TextView SongTitle;
    ProgressBar stepsProgress;

    APISpec ServerAPI;
    PreferencesService prefs;

    String groupCode = "<na>";
    String groupName = "<na>";
    int stepsTaken = 0;
    Song currentSong;
    MediaPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_and_workout);

        groupCode = getIntent().getStringExtra("GROUP_CODE");
        groupName = getIntent().getStringExtra("GROUP_NAME");

        ServerAPI = new APIService();
        prefs = new PreferencesService(this);

        leaderboardListView = (ListView) findViewById(R.id.playerProgress);
        leaderboardListView.setItemsCanFocus(false);
        leaderboardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        stepsDifferenceTextView = (TextView) findViewById(R.id.remainingSteps);

        stepsTakenTextView = (TextView) findViewById(R.id.stepCount);
        stepsProgress = (ProgressBar) findViewById(determinateBar);
        speedTextView = (TextView) findViewById(R.id.speed);

        currentSong = null;

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }

        this.setTitle(groupName + " - " + groupCode);

        ActivityCompat.requestPermissions(PlaylistAndWorkoutActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(PlaylistAndWorkoutActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

         /*
        Speedometer implementation - requestLocationUpdates() is called when location of the user changes,
                                     which in turn invokes onLocationChanged()
         */
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        beginSyncTask();



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
        stepsTakenTextView.setText(String.valueOf(event.values[0]).substring(0, String.valueOf(event.values[0]).length() - 2));
        stepsTaken = Integer.parseInt(stepsTakenTextView.getText().toString()) % 10000;
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

    void beginSyncTask() {
        final Handler h = new Handler();
        final int delay = 5000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                final Runnable runnable = this;
                ServerAPI.updateScore(groupCode, prefs.getDeviceID(), stepsTaken, new RequestHandler<Member>() {
                    @Override
                    public void callback(Member result) {
                        ServerAPI.groupInformation(groupCode, new RequestHandler<Group>() {
                            @Override
                            public void callback(Group result) {
                                if (result != null) {
                                    setLeaderboard(result);
                                    setStepsProgress(stepsTaken);
                                    setStepsDifference(result, stepsTaken);
                                    updatePlaylist(result.getCurrentSong());
                                }
                                h.postDelayed(runnable, delay);
                            }
                        });
                    }
                });
            }
        }, 0);
    }

    void setLeaderboard(Group group) {
        ArrayList<String> memberInformation = new ArrayList<String>();
        if(group != null) {
            for (Member member : group.getSortedMembers()) {
                memberInformation.add(member.getName() + "                                       " + member.getScore());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.playerlistitem, R.id.playerName, memberInformation);
        leaderboardListView.setAdapter(adapter);
    }

    void setStepsProgress(int currentSteps) {
        stepsProgress.setProgress(currentSteps / 100);
        stepsTakenTextView.setText(currentSteps + "");
    }

    void setStepsDifference(Group group, int currentSteps) {
        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (Member member : group.getSortedMembers()) {
            scores.add(member.getScore());
        }

        if (scores.size() == 1) {
            stepsDifferenceTextView.setText("You lead by " + currentSteps + " steps");
            stepsDifferenceTextView.setTextColor(Color.GREEN);
        } else if (currentSteps >= (scores.get(0))) {
            int diff = currentSteps - scores.get(1);
            stepsDifferenceTextView.setText("You lead by " + diff + " steps");
            stepsDifferenceTextView.setTextColor(Color.GREEN);
        } else {
            int diff = scores.get(0) - currentSteps;
            stepsDifferenceTextView.setText("You trail by " + diff + " steps");
            stepsDifferenceTextView.setTextColor(Color.RED);
        }
    }

    void updatePlaylist(Song currentSong) {
        if (currentSong == null) {
            return;
        }
        if (this.currentSong == null || !this.currentSong.getEndTime().equals(currentSong.getEndTime())) {
            playSong(currentSong);
        }
        this.currentSong = currentSong;
    }

    public static int getDateDiff(Date date, long Duration, TimeUnit timeUnit) {
        //long diffInMillies = date2.getTime() - date1.getTime();
        long diffInMillies = date.getTime() - Duration;
        return (int)timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }


    void playSong(final Song song) {
        if (player != null) {
            player.stop();
        }
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//              TODO: if we join a group late, we need to skip forward
                mp.start();
                int songDuration = mp.getDuration();
                Date Present = new Date();
                System.out.println("**********Song Duration: "+songDuration+" ****End Time: "+song.getEndTime().getTime()+" *****Present: "+Present.getTime()+" End Time Date: "+song.getEndTime());
                System.out.println("**********ET - SD: "+(getDateDiff(song.getEndTime(),songDuration,TimeUnit.MILLISECONDS)));
                //mp.seekTo(10000);
                System.out.println("**********Seek Time: "+(getDateDiff(Present, getDateDiff(song.getEndTime(),songDuration,TimeUnit.MILLISECONDS),TimeUnit.MILLISECONDS)));
                //mp.seekTo(songDuration - (getDateDiff(song.getEndTime(),TimeUnit.MILLISECONDS)));
                mp.seekTo(getDateDiff(Present, getDateDiff(song.getEndTime(),songDuration,TimeUnit.MILLISECONDS),TimeUnit.MILLISECONDS));

            }
        });
        SongTitle = (TextView) findViewById(R.id.songTitle);
        System.out.println("*******Song: "+song.getUrl().substring(song.getUrl().lastIndexOf("/"),song.getUrl().indexOf(".mp3")));
        SongTitle.setText(song.getUrl().substring(song.getUrl().lastIndexOf("/")+1,song.getUrl().indexOf(".mp3")));
        try {
            if(song != null) {
                player.setDataSource(APIService.BASE_URL + song.getUrl());
                player.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
   Method to update speed of the user in display screen, when location changes
    */

    @Override
    public void onLocationChanged(Location location) {
        TextView txt= (TextView) this.findViewById(R.id.speed);

        if(location==null)
        {
            txt.setText("0.0 m/s");

        }
        else
        {
            //double currSpeed= 300.00;
            double currSpeed=location.getSpeed();
            txt.setText(currSpeed + " m/s");

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        player.stop();
        finish();
        return;
    }
}
