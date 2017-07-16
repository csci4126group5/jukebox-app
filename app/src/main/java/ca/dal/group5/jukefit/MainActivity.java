package ca.dal.group5.jukefit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.dal.group5.jukefit.API.APIService;
import ca.dal.group5.jukefit.API.APISpec;
import ca.dal.group5.jukefit.API.RequestHandler;
import ca.dal.group5.jukefit.Dialog.CreateGroupDialog;
import ca.dal.group5.jukefit.Dialog.JoinGroupDialog;
import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Preferences.PreferencesService;

public class MainActivity extends AppCompatActivity
        implements CreateGroupDialog.CreateGroupDialogListener, JoinGroupDialog.JoinGroupDialogListener {

    Activity context;

    ListView groupListView;
    Button createGroupButton;
    Button joinGroupButton;
    Button manageSongsButton;

    ArrayList<Pair<String, Group>> savedGroups;
    ArrayAdapter<String> groupsListAdapter;

    APISpec ServerAPI;
    PreferencesService prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        ServerAPI = new APIService();
        prefs = new PreferencesService(this);

        groupListView = (ListView) findViewById(R.id.groupList);
        createGroupButton = (Button) findViewById(R.id.createGroupBtn);
        joinGroupButton = (Button) findViewById(R.id.joinGroupBtn);
        manageSongsButton = (Button) findViewById(R.id.manageSongsBtn);

        savedGroups = new ArrayList<Pair<String, Group>>();
        getGroups();//get group list from SharedPreferences
        groupsListAdapter = new ArrayAdapter<String>(this, R.layout.grouplistitem, R.id.groupName, new ArrayList<String>());
        groupListView.setAdapter(groupsListAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), PlaylistAndWorkoutActivity.class);
                intent.putExtra("GROUP_CODE", savedGroups.get(position).second.getCode());
                intent.putExtra("GROUP_NAME", savedGroups.get(position).first);
                startActivity(intent);
            }
        });
        this.setTitle("JukeFit");
        populateListView();
    }

    private void populateListView() {
        List<String> groupNames = new ArrayList<String>();
        for (Pair<String, Group> pair : savedGroups) {
            groupNames.add(pair.first);
        }

        groupsListAdapter.clear();
        groupsListAdapter.addAll(groupNames);
        groupsListAdapter.notifyDataSetChanged();
    }

    public void onCreateGroup(View v) {
        new CreateGroupDialog().show(getFragmentManager(), "createGroup");
    }

    public void onJoinGroup(View v) {
        new JoinGroupDialog().show(getFragmentManager(), "joinGroup");
    }

    public void onManageSongs(View v) {
        startActivity(new Intent(context, AddSongsActivity.class));
    }

    @Override
    public void onGroupJoined(final String nickname, String groupCode, String username) {
        ServerAPI.joinGroup(groupCode, username, prefs.getDeviceID(), new RequestHandler<Group>() {
            @Override
            public void callback(Group joined) {
                if (joined == null) {
                    Toast.makeText(context, "Incorrect group code! Please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                savedGroups.add(new Pair<String, Group>(nickname, joined));
                populateListView();
                saveGroups();//Save list to the SharedPreferences
            }
        });
    }

    @Override
    public void onGroupCreated(final String nickname, final String username) {
        final Context context = this;
        ServerAPI.createGroup(new RequestHandler<Group>() {
            @Override
            public void callback(final Group created) {
                if (created.getCode() != null) {
                    ServerAPI.joinGroup(created.getCode(), username, prefs.getDeviceID(), new RequestHandler<Group>() {
                        @Override
                        public void callback(Group joined) {
                            savedGroups.add(new Pair<String, Group>(nickname, joined));
                            populateListView();
                            saveGroups();//Save list to the SharedPreferences
                        }
                    });
                }
            }
        });
    }

    public void saveGroups() { //convert the group list to string of nickname and groupCode
        String listOfCodes = "";
        for (Pair<String, Group> groupPair : savedGroups) {
            listOfCodes += "," + groupPair.first + "|" + groupPair.second.getCode();
        }
        prefs.setString("listOfCode", listOfCodes);
    }

    public void getGroups() { //retrieve the list and get the group info from the APIServices
        String listOfCode = prefs.getString("listOfCode");
        if (!listOfCode.isEmpty()) {
            for (String groupPair : listOfCode.split(","))
                if (!groupPair.isEmpty()) {
                    final String nickname = groupPair.substring(0, groupPair.indexOf('|'));
                    String code = groupPair.substring(groupPair.indexOf('|') + 1);
                    ServerAPI.groupInformation(code, new RequestHandler<Group>() {
                        @Override
                        public void callback(Group result) {
                            if (result == null) {
                                return;
                            }
                            savedGroups.add(new Pair<String, Group>(nickname, result));
                        }
                    });
                }
            populateListView();
        }
    }


}
