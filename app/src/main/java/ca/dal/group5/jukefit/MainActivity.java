package ca.dal.group5.jukefit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.dal.group5.jukefit.Model.Group;

public class MainActivity extends AppCompatActivity {

    Activity context;

    ListView groupListView;
    Button createGroupButton;
    Button joinGroupButton;
    Button manageSongsButton;

    ArrayList<Group> savedGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        savedGroups = new ArrayList<Group>();

        groupListView = (ListView) findViewById(R.id.groupList);

        createGroupButton = (Button) findViewById(R.id.createGroupBtn);
        joinGroupButton = (Button) findViewById(R.id.joinGroupBtn);
        manageSongsButton = (Button) findViewById(R.id.manageSongsBtn);

        this.setTitle("JukeFit");
        populateListView();
    }

    private void populateListView() {
        setContentView(R.layout.activity_main);

        List<String> groupNames = new ArrayList<String>();
        for (Group group : savedGroups) {
            groupNames.add(group.getCode());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout, groupNames);
        groupListView.setAdapter(adapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), PlaylistAndWorkoutActivity.class);
                intent.putExtra("GROUP_CODE", savedGroups.get(position).getCode());
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
//                setContentView(R.layout.joingroup);
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
    }

    public void onCreateGroup(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("Create Group");
        builder.setView(R.layout.creategroup);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
//        dialog.show();
//        setContentView(R.layout.creategroup);
//
//        Button newGroup = (Button) findViewById(createNewGroup);
//
//        newGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            /*
//            logic to create group
//            */
//            setContentView(R.layout.activity_main);
//
//            populateListView();
//
//            }
//        });
    }

    public void onJoinGroup(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("Join Group");
        builder.setView(R.layout.joingroup);
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onManageSongs(View v) {
        startActivity(new Intent(context, AddSongsActivity.class));
    }
}
