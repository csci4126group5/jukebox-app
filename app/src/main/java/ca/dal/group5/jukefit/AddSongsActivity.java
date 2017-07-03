package ca.dal.group5.jukefit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.os.Environment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.dal.group5.jukefit.API.APIService;
import ca.dal.group5.jukefit.API.RequestHandler;
import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Song;
import ca.dal.group5.jukefit.Preferences.PreferencesService;

public class AddSongsActivity extends AppCompatActivity {

    Activity context;
    public String fullPath;
    File file;
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    ListView listview;
    //public List<SongWrapper> SongWrapperList;
    public List<String> SelectedSongs;
    PreferencesService prefs;
    File SongFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = new PreferencesService(this);

        //SongWrapperList = new ArrayList<SongWrapper>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddSongs);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService ServerAPI = new APIService();
                ServerAPI.mp3Upload(prefs.getDeviceID(), SongFile, new RequestHandler<Song>() {
                    @Override
                    public void callback(Song result) {
                        System.out.println("**********" + SongFile.getAbsolutePath());
                        //System.out.println("**********"+result.getUrl());
                    }
                });
                Intent intent = new Intent(AddSongsActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
                Toast.makeText(AddSongsActivity.this, "Song Added!", Toast.LENGTH_LONG).show();
            }
        });

        ActivityCompat.requestPermissions(AddSongsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(AddSongsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
        } else {
            fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JukeFit/";
            boolean success = true;
            try {
                file = new File(fullPath);
                if (!file.exists())
                    success = file.mkdirs();
                if (success) {
                    System.out.println("Full Path: " + fullPath);
                    System.out.println("Success");
                } else {
                    System.out.println("Failure");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            if (file.listFiles() != null) {
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
                System.out.println("List File Size: " + listFile[i].length());
                if (listFile[i].length() != 0) {
                    FilePathStrings[i] = listFile[i].getAbsolutePath();
                    FileNameStrings[i] = listFile[i].getName();
                    System.out.println("FilePathStrings: " + FilePathStrings[i]);
                    System.out.println("FileNameStrings: " + FileNameStrings[i]);
                    System.out.println("List File Size 2: " + listFile[i].length());
                }
            }
        }
        if (FilePathStrings != null) {

            listview = (ListView) findViewById(R.id.SongListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.songlistitem, R.id.SongName, FileNameStrings);
            listview.setAdapter(adapter);
            //listview.setItemsCanFocus(false);
            listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                CheckedTextView ctv = (CheckedTextView) view;
                SongFile = new File(listFile[position].getAbsolutePath());
                //Toast.makeText(AddSongsActivity.this, listFile[position].getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //Toast.makeText(AddSongsActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /*private class SongWrapper {

        String SongPath;
        Boolean isSelected;

        SongWrapper(String SPath) {
            SongPath = SPath;
            isSelected = false;
        }

        public String getSongPath() {
            return SongPath;
        }

        public void setSongPath(String songPath) {
            SongPath = songPath;
        }

        public Boolean getSelected() {
            return isSelected;
        }

        public void setSelected(Boolean selected) {
            isSelected = selected;
        }
    }*/
}
