package ca.dal.group5.jukefit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
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

public class PlaylistAndWorkoutActivity extends AppCompatActivity implements SensorEventListener {

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
        if (currentSong != null && (this.currentSong == null || !this.currentSong.getEndTime().equals(currentSong.getEndTime()))) {
            playSong(currentSong);
        }
        this.currentSong = currentSong;
    }

    public static int getDateDiff(Date date, TimeUnit timeUnit) {
        //long diffInMillies = date2.getTime() - date1.getTime();
        long diffInMillies = date.getTime() - new Date().getTime();
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

                //mp.seekTo(10000);
                mp.seekTo(songDuration - (getDateDiff(song.getEndTime(),TimeUnit.MILLISECONDS)));
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
}
