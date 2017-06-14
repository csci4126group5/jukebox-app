package ca.dal.group5.jukefit;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

import ca.dal.group5.jukefit.API.APISpec;
import ca.dal.group5.jukefit.API.MockAPI;
import ca.dal.group5.jukefit.Dialog.CreateGroupDialog;
import ca.dal.group5.jukefit.Dialog.JoinGroupDialog;
import ca.dal.group5.jukefit.Model.Group;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        groupListView = (ListView) findViewById(R.id.groupList);
        createGroupButton = (Button) findViewById(R.id.createGroupBtn);
        joinGroupButton = (Button) findViewById(R.id.joinGroupBtn);
        manageSongsButton = (Button) findViewById(R.id.manageSongsBtn);

        savedGroups = new ArrayList<Pair<String, Group>>();
        groupsListAdapter = new ArrayAdapter<String>(this, R.layout.grouplistitem, R.id.groupName, new ArrayList<String>());
        groupListView.setAdapter(groupsListAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), PlaylistAndWorkoutActivity.class);
                intent.putExtra("GROUP_CODE", savedGroups.get(position).second.getCode());
                startActivity(intent);
//                // 1. Instantiate an AlertDialog.Builder with its constructor
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                // 2. Chain together various setter methods to set the dialog characteristics
//                builder.setMessage(R.string.dialog_message)
//                        .setTitle(R.string.dialog_title);
//
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User clicked OK button
//                    }
//                });
//                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//
//                // 3. Get the AlertDialog from create()
//                AlertDialog dialog = builder.create();


//                TextView textView = (TextView) view;
//                //String message = "You clicked # " + position + ", which is string: " + textView.getText().toString();
//                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
//                setContentView(R.grouplistitem.joingroup);
//                TextView group = (TextView) findViewById(selectedGroup);
//                String gName = textView.getText().toString();
//                group.setText(gName);
//
//                Button addMember = (Button) findViewById(R.id.join);
//                addMember.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        EditText password = (EditText) findViewById(passcode);
//
//                        Button passwordVal = (Button) findViewById(passcodeValidation);
//
//                        password.setVisibility(View.VISIBLE);
//                        passwordVal.setVisibility(View.VISIBLE);
//                        Button addMember = (Button) findViewById(R.id.join);
//
//                        addMember.setVisibility(View.INVISIBLE);
//
//                        // Retrieve passcode from DB for validation
////                        int pass = 123;
////
////                        if (Integer.parseInt(String.valueOf(password.getText())) == pass) {
////
////                        } else {
////
////                        }
//                    }
//                });
            }
        });

        ServerAPI = new MockAPI();

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
    public void onGroupJoined(String nickname, String groupCode) {
        Log.d("joining", nickname + " " + groupCode);
        Group joined = ServerAPI.joinGroup(groupCode, "YOU", "DEVICE_ID");
        savedGroups.add(new Pair<String, Group>(nickname, joined));
        populateListView();
    }

    @Override
    public void onGroupCreated(String nickname) {
        Log.d("creating", nickname);
        Group created = ServerAPI.createGroup();
        Group joined = ServerAPI.joinGroup(created.getCode(), "YOU", "DEVICE_ID");
        savedGroups.add(new Pair<String, Group>(nickname, joined));
        populateListView();
    }
}
