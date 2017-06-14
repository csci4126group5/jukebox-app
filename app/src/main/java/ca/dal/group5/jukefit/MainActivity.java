package ca.dal.group5.jukefit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import static ca.dal.group5.jukefit.R.id.AddSongsActivity;
import static ca.dal.group5.jukefit.R.id.createGroup;
import static ca.dal.group5.jukefit.R.id.createNewGroup;
import static ca.dal.group5.jukefit.R.id.passcode;
import static ca.dal.group5.jukefit.R.id.passcodeValidation;
import static ca.dal.group5.jukefit.R.id.selectedGroup;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        setContentView(R.layout.activity_main);
        activity.setTitle("Music App");
        populateListView();

    }

    private void populateListView() {
        setContentView(R.layout.activity_main);

        String[] myItems={"Group1","Group2","Group3","Group4","Group5"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.layout,myItems);

        ListView list=(ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                //String message = "You clicked # " + position + ", which is string: " + textView.getText().toString();
                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                setContentView(R.layout.joingroup);
                TextView group=(TextView) findViewById(selectedGroup);
                String gName=textView.getText().toString();
               group.setText(gName);

               Button addMember=(Button) findViewById(R.id.join);
                addMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText password= (EditText) findViewById(passcode);

                        Button passwordVal=(Button) findViewById(passcodeValidation);

                        password.setVisibility(View.VISIBLE);
                        passwordVal.setVisibility(View.VISIBLE);
                        Button addMember=(Button) findViewById(R.id.join);

                        addMember.setVisibility(View.INVISIBLE);

                     /*   // Retrieve passcode from DB for validation
                        int pass=123;

                        if(Integer.parseInt(String.valueOf(password.getText())) == pass)
                        {

                        }
                        else
                        {

                        }*/


                    }
                });

            }



        });


        Button createG=(Button) findViewById(createGroup);

     createG.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        setContentView(R.layout.creategroup);

        Button newGroup= (Button) findViewById(createNewGroup);

        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                logic to create group
                */
                setContentView(R.layout.activity_main);

                populateListView();

            }
        });

    }
});

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.songlistFB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),AddSongsActivity.class));
            }
        });


    }


}
